package org.example.pdnight.domain.post.infra.post;

import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.post.domain.post.Post;
import org.example.pdnight.domain.post.domain.post.PostDocument;
import org.example.pdnight.domain.post.domain.post.PostReader;
import org.example.pdnight.domain.post.enums.AgeLimit;
import org.example.pdnight.domain.post.enums.Gender;
import org.example.pdnight.domain.post.enums.JoinStatus;
import org.example.pdnight.domain.post.enums.PostStatus;
import org.example.pdnight.global.common.enums.JobCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

import static org.example.pdnight.domain.post.domain.post.QPost.post;
import static org.example.pdnight.domain.post.domain.post.QPostLike.postLike;
import static org.example.pdnight.domain.post.domain.post.QPostTag.postTag;
import static org.example.pdnight.domain.post.domain.tag.QTag.tag;

@Repository
@RequiredArgsConstructor
public class PostReaderImpl implements PostReader {

    private final JPAQueryFactory queryFactory;
    private final PostESRepository postESRepository;
    private final ElasticsearchOperations elasticsearchOperations;


    @Override
    public Optional<PostDocument> findByIdES(Long postId) {
        return postESRepository.findById(postId);
    }

    private Map<Long, List<String>> getPostTagMap(List<Long> postIds) {
        if (postIds == null || postIds.isEmpty()) return Collections.emptyMap();
        return queryFactory
                .select(postTag.post.id, tag.name)
                .from(postTag)
                .join(tag).on(postTag.tagId.eq(tag.id))
                .where(postTag.post.id.in(postIds))
                .fetch()
                .stream()
                .collect(Collectors.groupingBy(
                        tuple -> tuple.get(postTag.post.id),
                        Collectors.mapping(tuple -> tuple.get(tag.name), Collectors.toList())

                ));
    }

    // 내 좋아요 게시글 목록 조회
    @Override
    public Page<PostDocument> getMyLikePostES(Long userId, Pageable pageable) {
        List<Query> mustQueries = new ArrayList<>();

        mustQueries.add(NestedQuery.of(post -> post.path("postLikes").query(q -> q
                .term(t -> t.field("postLikes.userId").value(userId.toString()))))._toQuery());
        mustQueries.add(TermQuery.of(post -> post.field("isDeleted").value(false))._toQuery());

        BoolQuery.Builder boolQueryBuilder = new BoolQuery.Builder().must(mustQueries);
        Query query = boolQueryBuilder.build()._toQuery();

        // NativeQuery 생성 (정렬 + 페이징)
        NativeQuery searchQuery = NativeQuery.builder()
                .withQuery(query)
                .withPageable(pageable != null ? pageable : Pageable.unpaged())
                .build();

        SearchHits<PostDocument> search = elasticsearchOperations.search(searchQuery, PostDocument.class);

        List<PostDocument> list = search.stream().map(SearchHit::getContent).toList();
        long total = search.getTotalHits();

        return new PageImpl<>(list, pageable, total);
    }

    // 참여 신청한 게시글 목록 조회
    @Override
    public Page<PostDocument> getConfirmedPostES(Long userId, JoinStatus joinStatus, Pageable pageable) {
        List<Query> mustQueries = new ArrayList<>();
        mustQueries.add(NestedQuery.of(post -> post.path("postParticipants").query(q -> q
                .term(t -> t.field("postParticipants.userId").value(userId.toString()))))._toQuery());
        if (joinStatus != null) {
            mustQueries.add(NestedQuery.of(post -> post.path("postParticipants").query(q -> q
                    .term(t -> t.field("postParticipants.status").value(joinStatus.name()))))._toQuery());
        }
        mustQueries.add(TermQuery.of(post -> post.field("isDeleted").value(false))._toQuery());

        BoolQuery.Builder boolQueryBuilder = new BoolQuery.Builder().must(mustQueries);
        Query query = boolQueryBuilder.build()._toQuery();

        // NativeQuery 생성 (정렬 + 페이징)
        NativeQuery searchQuery = NativeQuery.builder()
                .withQuery(query)
                .withPageable(pageable != null ? pageable : Pageable.unpaged())
                .build();

        SearchHits<PostDocument> search = elasticsearchOperations.search(searchQuery, PostDocument.class);

        List<PostDocument> list = search.stream().map(SearchHit::getContent).toList();
        long total = search.getTotalHits();

        return new PageImpl<>(list, pageable, total);
    }

    // 게시글 검색 목록 조회
    @Override
    public Page<PostDocument> findPostsBySearchES(
            Pageable pageable,
            Integer maxParticipants,
            AgeLimit ageLimit,
            JobCategory jobCategoryLimit,
            Gender genderLimit
    ) {

        List<Query> mustQueries = new ArrayList<>();
        mustQueries.add(TermQuery.of(post -> post.field("status").value(PostStatus.OPEN.name()))._toQuery());
        mustQueries.add(TermQuery.of(post -> post.field("isDeleted").value(false))._toQuery());

        if (maxParticipants != null) {
            RangeQuery rangeQuery = new RangeQuery.Builder()
                    .number(d -> d
                            .field("maxParticipants")
                            .gte(Double.valueOf(maxParticipants))
                    )
                    .build();
            mustQueries.add(rangeQuery._toQuery());
        }
        if (ageLimit != null) {
            mustQueries.add(TermQuery.of(post -> post.field("ageLimit").value(ageLimit.name()))._toQuery());
        }
        if (jobCategoryLimit != null) {
            mustQueries.add(TermQuery.of(post -> post.field("jobCategoryLimit").value(jobCategoryLimit.name()))._toQuery());
        }
        if (genderLimit != null) {
            mustQueries.add(TermQuery.of(post -> post.field("genderLimit").value(genderLimit.name()))._toQuery());
        }

        BoolQuery.Builder boolQueryBuilder = new BoolQuery.Builder().must(mustQueries);
        Query query = boolQueryBuilder.build()._toQuery();

        // 정렬: 최근 날짜 > id 역순
        List<SortOptions> sortOptions = List.of(
                SortOptions.of(s -> s.field(post -> post.field("createdAt").order(SortOrder.Desc)))
        );

        // NativeQuery 생성 (정렬 + 페이징)
        NativeQuery searchQuery = NativeQuery.builder()
                .withQuery(query)
                .withSort(sortOptions)
                .withPageable(pageable != null ? pageable : Pageable.unpaged())
                .build();

        SearchHits<PostDocument> search = elasticsearchOperations.search(searchQuery, PostDocument.class);

        List<PostDocument> list = search.stream().map(SearchHit::getContent).toList();
        long total = search.getTotalHits();

        return new PageImpl<>(list, pageable, total);
    }

    // 내가 작성 한 게시물 조회
    @Override
    public Page<PostDocument> getWrittenPostES(
            Long userId,
            Pageable pageable
    ) {
        List<Query> mustQueries = new ArrayList<>();
        mustQueries.add(TermQuery.of(post -> post.field("authorId").value(userId.toString()))._toQuery());
        mustQueries.add(TermQuery.of(post -> post.field("isDeleted").value(false))._toQuery());

        BoolQuery.Builder boolQueryBuilder = new BoolQuery.Builder().must(mustQueries);
        Query query = boolQueryBuilder.build()._toQuery();

        // NativeQuery 생성 (정렬 + 페이징)
        NativeQuery searchQuery = NativeQuery.builder()
                .withQuery(query)
                .withPageable(pageable != null ? pageable : Pageable.unpaged())
                .build();

        SearchHits<PostDocument> search = elasticsearchOperations.search(searchQuery, PostDocument.class);

        List<PostDocument> list = search.stream().map(SearchHit::getContent).toList();
        long total = search.getTotalHits();

        return new PageImpl<>(list, pageable, total);
    }

    // 게시글 단건 조회
    @Override
    public Optional<Post> findById(Long postId) {
        Post findPost = queryFactory
                .select(post)
                .from(post)
                .where(post.id.eq(postId))
                .fetchFirst();
        return Optional.ofNullable(findPost);
    }

    // 추천 게시글 목록 조회 -> 추천 알고리즘
    @Override
    public Page<Post> getSuggestedPost(Long userId, Pageable pageable) {
        List<Post> contents = queryFactory
                .select(post)
                .from(post)
                .leftJoin(postLike).on(postLike.post.eq(post)) // 좋아요 조인
                .groupBy(post.id)
                .where(postLike.userId.eq(userId)
                        .and(post.status.eq(PostStatus.OPEN))
                        .and(post.isDeleted.eq(false)))
                .orderBy(postLike.count().desc()) // 좋아요 수 내림차순 정렬
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long count = queryFactory
                .select(post.countDistinct())
                .from(post)
                .leftJoin(postLike).on(postLike.post.eq(post))
                .where(postLike.userId.eq(userId))
                .fetchOne();

        return PageableExecutionUtils.getPage(contents, pageable, () -> Optional.ofNullable(count).orElse(0L));
    }

    // 초대받은 리스트
    @Override
    public Page<PostDocument> getMyInvitedES(Long userId, Pageable pageable) {
        List<Query> mustQueries = new ArrayList<>();
        mustQueries.add(NestedQuery.of(post -> post.path("invites").query(q -> q
                .term(t -> t.field("invites.inviteeId").value(userId.toString()))))._toQuery());

        mustQueries.add(TermQuery.of(post -> post.field("isDeleted").value(false))._toQuery());

        BoolQuery.Builder boolQueryBuilder = new BoolQuery.Builder().must(mustQueries);
        Query query = boolQueryBuilder.build()._toQuery();

        // NativeQuery 생성 (정렬 + 페이징)
        NativeQuery searchQuery = NativeQuery.builder()
                .withQuery(query)
                .withPageable(pageable != null ? pageable : Pageable.unpaged())
                .build();

        SearchHits<PostDocument> search = elasticsearchOperations.search(searchQuery, PostDocument.class);

        List<PostDocument> list = search.stream().map(SearchHit::getContent).toList();
        long total = search.getTotalHits();

        return new PageImpl<>(list, pageable, total);
    }

    // 내가 초대한 리스트
    @Override
    public List<PostDocument> getMyInviteES(Long userId) {
        List<Query> mustQueries = new ArrayList<>();
        mustQueries.add(NestedQuery
                .of(post -> post.path("invites")
                        .query(q -> q.term(t -> t.field("invites.inviterId")
                                .value(userId.toString()))))
                ._toQuery());
        mustQueries.add(TermQuery.of(post -> post.field("isDeleted").value(false))._toQuery());

        BoolQuery.Builder boolQueryBuilder = new BoolQuery.Builder().must(mustQueries);
        Query query = boolQueryBuilder.build()._toQuery();

        // NativeQuery 생성 (정렬 + 페이징)
        NativeQuery searchQuery = NativeQuery.builder()
                .withQuery(query)
                .build();

        SearchHits<PostDocument> search = elasticsearchOperations.search(searchQuery, PostDocument.class);

        List<PostDocument> list = search.stream().map(SearchHit::getContent).toList();

        return list;
    }
}