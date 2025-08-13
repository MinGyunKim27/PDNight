package org.example.pdnight.domain.post.infra.post;

import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.RangeQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.TermQuery;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.post.domain.post.Post;
import org.example.pdnight.domain.post.domain.post.PostDocument;
import org.example.pdnight.domain.post.domain.post.PostReader;
import org.example.pdnight.domain.post.enums.AgeLimit;
import org.example.pdnight.domain.post.enums.Gender;
import org.example.pdnight.domain.post.enums.JoinStatus;
import org.example.pdnight.domain.post.enums.PostStatus;
import org.example.pdnight.domain.post.presentation.dto.response.InviteResponse;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.example.pdnight.domain.post.domain.post.QPostLike.postLike;

@Repository
@RequiredArgsConstructor
public class PostReaderImpl implements PostReader {

    private final JPAQueryFactory queryFactory;
    private final PostESRepository postESRepository;
    private final ElasticsearchOperations elasticsearchOperations;

    // 게시글 단건 조회 + 참여자 목록
    @Override
    public Optional<Post> findByIdWithParticipants(Long postId) {
        Post findPost = queryFactory
                .select(QPost.post)
                .from(QPost.post)
                .leftJoin(QPost.post.postParticipants, postParticipant).fetchJoin()
                .where(QPost.post.id.eq(postId))
                .fetchOne();

        return Optional.ofNullable(findPost);
    }

    // 게시글 단건 조회
    @Override
    public Optional<Post> findById(Long postId) {
        Post findPost = queryFactory
                .select(QPost.post)
                .from(QPost.post)
                .where(QPost.post.id.eq(postId))
                .fetchFirst();
        return Optional.ofNullable(findPost);
    }

    @Override
    public Optional<PostDocument> findByIdES(Long postId) {
        return postESRepository.findById(postId);
    }

    // 내 좋아요 게시글 목록 조회
    @Override
    public Page<PostResponse> getMyLikePost(Long userId, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();
        // 닫힌 상태가 아닐 때
        // builder.and(post.status.ne(PostStatus.CLOSED));
        builder.and(postLike.userId.eq(userId));

        // 삭제 상태가 아닐 때
        builder.and(QPost.post.isDeleted.eq(false));

        List<PostResponse> contents = queryFactory
                .select(new QPostResponse(
                        QPost.post.id,
                        QPost.post.authorId,
                        QPost.post.title,
                        QPost.post.timeSlot,
                        QPost.post.publicContent,
                        QPost.post.status,
                        QPost.post.maxParticipants,
                        QPost.post.genderLimit,
                        QPost.post.jobCategoryLimit,
                        QPost.post.ageLimit,
                        QPost.post.isFirstCome,
                        QPost.post.createdAt,
                        QPost.post.updatedAt
                ))
                .from(QPost.post)
                .join(postLike).on(postLike.post.eq(QPost.post))
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long count = queryFactory
                .select(QPost.post.count())
                .from(QPost.post)
                .join(postLike).on(postLike.post.eq(QPost.post))
                .where(builder)
                .fetchOne();

        return PageableExecutionUtils.getPage(contents, pageable, () -> Optional.ofNullable(count).orElse(0L));
    }

    // 내 좋아요 게시글 목록 조회
    @Override
    public Page<PostDocument> getMyLikePostES(Long userId, Pageable pageable) {
        List<Query> mustQueries = new ArrayList<>();
        mustQueries.add(TermQuery.of(post -> post.field("postLikes.userId").value(userId.toString()))._toQuery());
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
        mustQueries.add(TermQuery.of(post -> post.field("postParticipants.userId").value(userId.toString()))._toQuery());
        mustQueries.add(TermQuery.of(post -> post.field("postParticipants.status").value(joinStatus.toString()))._toQuery());
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
    public Page<PostResponse> getConfirmedPost(Long userId, JoinStatus joinStatus, Pageable pageable) {


        BooleanBuilder builder = new BooleanBuilder();

        builder.and(postParticipant.userId.eq(userId));

        //닫힌 상태가 아닐 때
        builder.and(QPost.post.status.ne(PostStatus.CLOSED));

        // 삭제 상태가 아닐 때
        builder.and(QPost.post.isDeleted.eq(false));

        if (joinStatus != null) {
            builder.and(postParticipant.status.eq(joinStatus));
        }

        List<PostResponse> content = queryFactory
                .select(new QPostResponse(
                        QPost.post.id,
                        QPost.post.authorId,
                        QPost.post.title,
                        QPost.post.timeSlot,
                        QPost.post.publicContent,
                        QPost.post.status,
                        QPost.post.maxParticipants,
                        QPost.post.genderLimit,
                        QPost.post.jobCategoryLimit,
                        QPost.post.ageLimit,
                        QPost.post.isFirstCome,
                        postParticipant.status,
                        postParticipant.createdAt,
                        QPost.post.createdAt,
                        QPost.post.updatedAt
                ))
                .from(QPost.post)
                .join(postParticipant).on(postParticipant.post.eq(QPost.post))
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long count = queryFactory
                .select(QPost.post.count())
                .from(QPost.post)
                .join(postParticipant).on(postParticipant.post.eq(QPost.post))
                .where(builder)
                .fetchOne();

        return PageableExecutionUtils.getPage(content, pageable, () -> Optional.ofNullable(count).orElse(0L));
    }

    // 게시글 검색 목록 조회
    @Override
    public Page<Post> findPostsBySearch(
            Pageable pageable,
            Integer maxParticipants,
            AgeLimit ageLimit,
            JobCategory jobCategoryLimit,
            Gender genderLimit
    ) {
        // 조건 누적용 BooleanBuilder
        BooleanBuilder builder = new BooleanBuilder();

        // 무조건 포함되는 조건
        builder.and(QPost.post.status.eq(PostStatus.OPEN));

        // 삭제 상태가 아닐 때
        builder.and(QPost.post.isDeleted.eq(false));

        // nullable 조건 추가
        if (maxParticipants != null) {
            builder.and(QPost.post.maxParticipants.goe(maxParticipants));
        }
        if (ageLimit != null) {
            builder.and(QPost.post.ageLimit.eq(ageLimit));
        }
        if (jobCategoryLimit != null) {
            builder.and(QPost.post.jobCategoryLimit.eq(jobCategoryLimit));
        }
        if (genderLimit != null) {
            builder.and(QPost.post.genderLimit.eq(genderLimit));
        }

        List<Post> contents = queryFactory
                .select(QPost.post)
                .from(QPost.post)
                .where(builder)
                .orderBy(QPost.post.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = Optional.ofNullable(
                queryFactory
                        .select(QPost.post.countDistinct())
                        .from(QPost.post)
                        .where(builder)
                        .fetchOne()
        ).orElse(0L);

        return new PageImpl<>(contents, pageable, total);
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
        mustQueries.add(TermQuery.of(post -> post.field("status").value("OPEN"))._toQuery());
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
//        if (maxParticipants != null) {
//            mustQueries.add(RangeQuery.of(post -> post.field("maxParticipants").gte(JsonData.of(maxParticipants)))._toQuery());
//        }
        if (ageLimit != null) {
            mustQueries.add(TermQuery.of(post -> post.field("ageLimit").value(ageLimit.toString()))._toQuery());
        }
        if (jobCategoryLimit != null) {
            mustQueries.add(TermQuery.of(post -> post.field("jobCategoryLimit").value(jobCategoryLimit.toString()))._toQuery());
        }
        if (genderLimit != null) {
            mustQueries.add(TermQuery.of(post -> post.field("genderLimit").value(genderLimit.toString()))._toQuery());
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
    public Page<Post> getWrittenPost(
            Long userId,
            Pageable pageable
    ) {
        BooleanBuilder builder = new BooleanBuilder();

        builder.and(QPost.post.authorId.eq(userId));
        // 삭제 상태가 아닐 때
        builder.and(QPost.post.isDeleted.eq(false));

        List<Post> contents = queryFactory
                .select(QPost.post)
                .from(QPost.post)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long count = queryFactory
                .select(QPost.post.count())
                .from(QPost.post)
                .where(builder)
                .fetchOne();

        return PageableExecutionUtils.getPage(contents, pageable, () -> Optional.ofNullable(count).orElse(0L));
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

    // 추천 게시글 목록 조회 -> 추천 알고리즘
    @Override
    public Page<Post> getSuggestedPost(Long userId, Pageable pageable) {
        List<Post> contents = queryFactory
                .select(QPost.post)
                .from(QPost.post)
                .leftJoin(postLike).on(postLike.post.eq(QPost.post)) // 좋아요 조인
                .groupBy(QPost.post.id)
                .where(postLike.userId.eq(userId)
                        .and(QPost.post.status.eq(PostStatus.OPEN))
                        .and(QPost.post.isDeleted.eq(false)))
                .orderBy(postLike.count().desc()) // 좋아요 수 내림차순 정렬
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long count = queryFactory
                .select(QPost.post.countDistinct())
                .from(QPost.post)
                .leftJoin(postLike).on(postLike.post.eq(QPost.post))
                .where(postLike.userId.eq(userId))
                .fetchOne();

        return PageableExecutionUtils.getPage(contents, pageable, () -> Optional.ofNullable(count).orElse(0L));
    }



//    // 초대받은 리스트
    @Override
    public Page<InviteResponse> getMyInvited(Long userId, Pageable pageable) {
        QInvite invite = QInvite.invite;

        JPQLQuery<InviteResponse> query = queryFactory
                .select(new QInviteResponse(
                        invite.id,
                        invite.inviteeId,
                        invite.post.id
                ))
                .from(invite)
                .where(invite.inviteeId.eq(userId) // 내가 초대 받은 경우
                        .and(QPost.post.isDeleted.eq(false)))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        JPQLQuery<Long> countQuery = queryFactory
                .select(invite.count())
                .from(invite)
                .where(invite.inviteeId.eq(userId));

        return PageableExecutionUtils.getPage(query.fetch(), pageable, countQuery::fetchOne);
    }

    // 초대받은 리스트
    @Override
    public Page<PostDocument> getMyInvitedES(Long userId, Pageable pageable) {
        List<Query> mustQueries = new ArrayList<>();
        mustQueries.add(TermQuery.of(post -> post.field("invites.inviteeId").value(userId.toString()))._toQuery());
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
//    @Override
//    public Page<InviteResponse> getMyInvite(Long userId, Pageable pageable) {
//
//        QInvite invite = QInvite.invite;
//
//        JPQLQuery<InviteResponse> query = queryFactory
//                .select(new QInviteResponse(
//                        invite.id,
//                        invite.inviteeId,
//                        invite.post.id
//                ))
//                .from(invite)
//                .where(invite.inviterId.eq(userId) // 내가 초대한 경우
//                        .and(QPost.post.isDeleted.eq(false)))
//                .offset(pageable.getOffset())
//                .limit(pageable.getPageSize());
//
//        JPQLQuery<Long> countQuery = queryFactory
//                .select(invite.count())
//                .from(invite)
//                .where(invite.inviterId.eq(userId));
//
//        return PageableExecutionUtils.getPage(query.fetch(), pageable, countQuery::fetchOne);
//    }

    // 내가 초대한 리스트
    @Override
    public Page<PostDocument> getMyInviteES(Long userId, Pageable pageable) {
        List<Query> mustQueries = new ArrayList<>();
        mustQueries.add(TermQuery.of(post -> post.field("invites.inviterId").value(userId.toString()))._toQuery());
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
}