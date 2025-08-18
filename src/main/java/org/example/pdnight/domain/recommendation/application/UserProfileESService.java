package org.example.pdnight.domain.recommendation.application;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.recommendation.infra.UserProfileDocument;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.ScriptType;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UserProfileESService {

    private static final String INDEX = "user_profiles";
    private static final DateTimeFormatter ISO_NO_OFFSET = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    private final ElasticsearchOperations operations;

    /** 사용자 프로필(취미/스킬) 수정 시 업서트 (읽지 않음) */
    public void upsertProfile(Long userId, List<String> newHobbys, List<String> newSkills) {
        String now = LocalDateTime.now().format(ISO_NO_OFFSET);

        Map<String, Object> params = new HashMap<>();
        params.put("h", newHobbys == null ? List.of() : newHobbys);
        params.put("s", newSkills == null ? List.of() : newSkills);
        params.put("now", now);

        String script = """
          if (ctx._source == null) { ctx._source = [:]; }
          ctx._source.hobbys = params.h;
          ctx._source.skills = params.s;
          if (ctx._source.likedTagCounts == null)  ctx._source.likedTagCounts  = [:];
          if (ctx._source.joinedTagCounts == null) ctx._source.joinedTagCounts = [:];
          ctx._source.updatedAt = params.now;
        """;

        Document upsertDoc = Document.create();
        upsertDoc.put("userId", userId);
        upsertDoc.put("hobbys", params.get("h"));
        upsertDoc.put("skills", params.get("s"));
        upsertDoc.put("likedTagCounts", Map.of());
        upsertDoc.put("joinedTagCounts", Map.of());
        upsertDoc.put("updatedAt", now);

        UpdateQuery uq = UpdateQuery.builder(String.valueOf(userId))
                .withIndex(INDEX)
                .withScript(script)
                .withScriptType(ScriptType.INLINE)
                .withLang("painless")
                .withParams(params)
                .withUpsert(upsertDoc)
                .build();

        operations.update(uq, IndexCoordinates.of(INDEX));
    }

    /** 좋아요 시, 게시물 태그 기반 likedTagCounts 증가 (읽지 않고 업서트) */
    public void incrementLikedTags(Long userId, Collection<String> tags) {
        if (tags == null || tags.isEmpty()) return;

        String now = LocalDateTime.now().format(ISO_NO_OFFSET);

        Map<String, Object> params = new HashMap<>();
        params.put("tags", new ArrayList<>(tags));
        params.put("now", now);

        String script = """
          if (ctx._source == null) ctx._source = [:];
          if (ctx._source.likedTagCounts == null) ctx._source.likedTagCounts = [:];

          for (t in params.tags) {
            if (t == null) continue;
            def tt = t.toString().trim();
            if (tt.length() == 0) continue;
            ctx._source.likedTagCounts[tt] = (ctx._source.likedTagCounts.containsKey(tt) ? ctx._source.likedTagCounts[tt] : 0) + 1;
          }

          if (ctx._source.hobbys == null) ctx._source.hobbys = [];
          if (ctx._source.skills == null) ctx._source.skills = [];
          if (ctx._source.joinedTagCounts == null) ctx._source.joinedTagCounts = [:];

          ctx._source.updatedAt = params.now;
        """;

        Document upsertDoc = Document.create();
        upsertDoc.put("userId", userId);
        upsertDoc.put("hobbys", List.of());
        upsertDoc.put("skills", List.of());
        upsertDoc.put("likedTagCounts", new HashMap<>());
        upsertDoc.put("joinedTagCounts", new HashMap<>());
        upsertDoc.put("updatedAt", now);

        UpdateQuery uq = UpdateQuery.builder(String.valueOf(userId))
                .withIndex(INDEX)
                .withScript(script)
                .withScriptType(ScriptType.INLINE)
                .withLang("painless")
                .withParams(params)
                .withUpsert(upsertDoc)
                .build();

        operations.update(uq, IndexCoordinates.of(INDEX));
    }

    /** 참가 확정 시, 게시물 태그 기반 joinedTagCounts 증가 (읽지 않고 업서트) */
    public void incrementJoinedTags(Long userId, Collection<String> tags) {
        if (tags == null || tags.isEmpty()) return;

        String now = LocalDateTime.now().format(ISO_NO_OFFSET);

        Map<String, Object> params = new HashMap<>();
        params.put("tags", new ArrayList<>(tags));
        params.put("now", now);

        String script = """
          if (ctx._source == null) ctx._source = [:];
          if (ctx._source.joinedTagCounts == null) ctx._source.joinedTagCounts = [:];

          for (t in params.tags) {
            if (t == null) continue;
            def tt = t.toString().trim();
            if (tt.length() == 0) continue;
            ctx._source.joinedTagCounts[tt] = (ctx._source.joinedTagCounts.containsKey(tt) ? ctx._source.joinedTagCounts[tt] : 0) + 1;
          }

          if (ctx._source.hobbys == null) ctx._source.hobbys = [];
          if (ctx._source.skills == null) ctx._source.skills = [];
          if (ctx._source.likedTagCounts == null) ctx._source.likedTagCounts = [:];

          ctx._source.updatedAt = params.now;
        """;

        Document upsertDoc = Document.create();
        upsertDoc.put("userId", userId);
        upsertDoc.put("hobbys", List.of());
        upsertDoc.put("skills", List.of());
        upsertDoc.put("likedTagCounts", new HashMap<>());
        upsertDoc.put("joinedTagCounts", new HashMap<>());
        upsertDoc.put("updatedAt", now);

        UpdateQuery uq = UpdateQuery.builder(String.valueOf(userId))
                .withIndex(INDEX)
                .withScript(script)
                .withScriptType(ScriptType.INLINE)
                .withLang("painless")
                .withParams(params)
                .withUpsert(upsertDoc)
                .build();

        operations.update(uq, IndexCoordinates.of(INDEX));
    }
}
