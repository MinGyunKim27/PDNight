package org.example.pdnight.domain.recommendation.infra;

import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.Script;
import co.elastic.clients.elasticsearch._types.Time;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.json.JsonData;
import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.post.domain.post.PostDocument;
import org.example.pdnight.domain.recommendation.domain.UserDocumentReaderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class UserDocumentReaderServiceImpl implements UserDocumentReaderService {
    private final ElasticsearchOperations operations;

    public Page<PostDocument> recommendForUserNormalized(UserProfileDocument profile, Pageable pageable) {
        if (profile == null) {
            return coldStart(pageable); // 프로필이 없으면 Cold-start: 인기 기반 추천
        }
        List<String> hobbies = safeList(profile.getHobbys());
        List<String> skills  = safeList(profile.getSkills());
        Map<String,Integer> likedTopK  = topK(profile.getLikedTagCounts(),  50);
        Map<String,Integer> joinedTopK = topK(profile.getJoinedTagCounts(), 50);

        // [2] 기본 필터 조건 설정
        // 모집 상태가 OPEN인 글만, 필요 시 시간 조건도 추가 가능
        List<Query> filters = new ArrayList<>();
        filters.add(TermQuery.of(t -> t.field("status").value("OPEN"))._toQuery());

        // [3] function_score 준비: 유저 신호별 가중치 부여
        List<FunctionScore> functions = new ArrayList<>();
        if (!hobbies.isEmpty()) {
            // 유저 취미 태그가 포함된 글 → 가중치 1.2
            functions.add(FunctionScore.of(f -> f
                    .filter(TermsQuery.of(t -> t.field("tagIds")
                            .terms(v -> v.value(hobbies.stream().map(FieldValue::of).toList())))._toQuery())
                    .weight(1.2)));
        }
        if (!skills.isEmpty()) {
            // 유저 스킬 태그가 포함된 글 → 가중치 1.5
            functions.add(FunctionScore.of(f -> f
                    .filter(TermsQuery.of(t -> t.field("tagIds")
                            .terms(v -> v.value(skills.stream().map(FieldValue::of).toList())))._toQuery())
                    .weight(1.5)));
        }
        if (!likedTopK.isEmpty()) {
            // 과거 "좋아요" 많이 누른 태그 → 가중치 2.0
            functions.add(FunctionScore.of(f -> f
                    .filter(TermsQuery.of(t -> t.field("tagIds")
                            .terms(v -> v.value(likedTopK.keySet().stream().map(FieldValue::of).toList())))._toQuery())
                    .weight(2.0)));
        }
        if (!joinedTopK.isEmpty()) {
            // 과거 "참여 확정" 태그 → 가중치 2.5
            functions.add(FunctionScore.of(f -> f
                    .filter(TermsQuery.of(t -> t.field("tagIds")
                            .terms(v -> v.value(joinedTopK.keySet().stream().map(FieldValue::of).toList())))._toQuery())
                    .weight(2.5)));
        }

        // [4] 최신성/인기 반영 (Decay + FieldValueFactor)
        // createdAt이 최신일수록 가중치↑ (7일 스케일)
        functions.add(FunctionScore.of(f -> f
                .exp(DecayFunction.of(df -> df
                        .date(d -> d
                                .field("createdAt")
                                .placement(DecayPlacement.<String, Time>of(p -> p
                                        .origin("now")
                                        .scale(Time.of(t -> t.time("7d")))
                                        .decay(0.5)
                                ))
                        )
                ))
                .weight(1.0)
        ));
        // 최근 7일 조회수(popView7d), 지원수(popApply7d) → log1p로 부스팅
        functions.add(FunctionScore.of(f -> f.fieldValueFactor(
                        FieldValueFactorScoreFunction.of(ff -> ff.field("popView7d")
                                .modifier(FieldValueFactorModifier.Log1p).missing(0.0)))
                .weight(0.5)));
        functions.add(FunctionScore.of(f -> f.fieldValueFactor(
                        FieldValueFactorScoreFunction.of(ff -> ff.field("popApply7d")
                                .modifier(FieldValueFactorModifier.Log1p).missing(0.0)))
                .weight(0.8)));

        // [5] 1차 후보 쿼리: function_score 적용
        Query candidate = FunctionScoreQuery.of(fs -> fs
                .query(BoolQuery.of(b -> b.filter(filters))._toQuery())
                .functions(functions)
                .scoreMode(FunctionScoreMode.Sum)   // 함수 점수 합산
                .boostMode(FunctionBoostMode.Sum)   // 최종 점수도 합산
        )._toQuery();

        // [6] 2차 리랭크: script_score
        // - 유저 태그 vs 글 태그 overlap 계산
        // - liked/joined는 log(1+count)로 가중치
        // - 정규화: sqrt(태그 수)로 나눔
        String painless = """
      def tags = new HashSet();
      for (def t : doc['tagIds']) { tags.add(t); }
      int n = Math.max(tags.size(), 1);

      int ov(List arr){ int c=0; for (def t : arr) if (tags.contains(t)) c++; return c; }

      double s = 0.0;
      s += params.wH  * ov(params.hobbies);
      s += params.wS  * ov(params.skills);

      for (entry in params.liked.entrySet()) {
        if (tags.contains(entry.getKey())) s += params.wLK * Math.log(1 + entry.getValue());
      }
      for (entry in params.joined.entrySet()) {
        if (tags.contains(entry.getKey())) s += params.wJN * Math.log(1 + entry.getValue());
      }

      return s / Math.sqrt(n);
    """;

        // [7] script_score에 넘겨줄 파라미터 정의
        Map<String, JsonData> params = new HashMap<>();
        params.put("hobbies", JsonData.of(hobbies));
        params.put("skills",  JsonData.of(skills));
        params.put("liked",   JsonData.of(likedTopK));
        params.put("joined",  JsonData.of(joinedTopK));
        params.put("wH",      JsonData.of(1.2));
        params.put("wS",      JsonData.of(1.5));
        params.put("wLK",     JsonData.of(2.0));
        params.put("wJN",     JsonData.of(2.5));

        Script script = Script.of(b -> b
                .source(painless)
                .lang("painless")
                .params(params)
        );

        Query scriptScore = ScriptScoreQuery.of(ss -> ss
                .query(candidate)
                .script(script)
        )._toQuery();

        // [8] 실제 쿼리 실행
        NativeQuery nq = NativeQuery.builder()
                .withQuery(scriptScore)
                .withPageable(pageable)
                .build();

        SearchHits<PostDocument> hits = operations.search(nq, PostDocument.class);

        // [9] 결과 매핑
        List<PostDocument> list = hits.stream().map(SearchHit::getContent).toList();

        // [10] Page 객체로 반환 (Spring Data 표준 리턴)
        return new PageImpl<>(list, pageable, hits.getTotalHits());
    }


    private static List<String> safeList(List<String> in) {
        return in == null ? List.of() : in;
    }

    private static Map<String,Integer> topK(Map<String,Integer> in, int k) {
        if (in == null || in.isEmpty()) return Map.of();
        return in.entrySet().stream()
                .sorted(Map.Entry.<String,Integer>comparingByValue().reversed())
                .limit(k)
                .collect(Collectors.toMap(
                        Map.Entry::getKey, Map.Entry::getValue, (a,b)->a, LinkedHashMap::new));
    }

    private Page<PostDocument> coldStart(Pageable pageable) {
        // 유저 신호 없을 때: 인기/최근성만으로 추천
        Query q = FunctionScoreQuery.of(fs -> fs
                .query(BoolQuery.of(b -> b.filter(
                        TermQuery.of(t -> t.field("status").value("OPEN"))._toQuery()
                ))._toQuery())
                .functions(f -> f.fieldValueFactor(ff -> ff.field("popApply7d")
                        .modifier(FieldValueFactorModifier.Log1p).missing(0.0)).weight(1.0))
                .functions(f -> f
                        .exp(DecayFunction.of(df -> df.date(d -> d.field("createdAt")   // ← 여기서 필드 지정
                                        .placement(DecayPlacement.<String, Time>of(p -> p
                                                .origin("now")
                                                .scale(Time.of(t -> t.time("7d")))
                                                .decay(0.5)   // 선택 (기본 0.5)
                                        ))
                                )
                        ))
                        .weight(0.5))
                .scoreMode(FunctionScoreMode.Sum)
                .boostMode(FunctionBoostMode.Sum)
        )._toQuery();

        NativeQuery nq = NativeQuery.builder().withQuery(q).withPageable(pageable).build();
        SearchHits<PostDocument> hits = operations.search(nq, PostDocument.class);
        List<PostDocument> list = hits.stream().map(SearchHit::getContent).toList();
        return new PageImpl<>(list, pageable, hits.getTotalHits());
    }
}
