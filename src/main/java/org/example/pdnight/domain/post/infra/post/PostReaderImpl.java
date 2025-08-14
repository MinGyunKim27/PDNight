package org.example.pdnight.domain.post.infra.post;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.post.domain.post.Post;
import org.example.pdnight.domain.post.domain.post.PostReader;
import org.example.pdnight.domain.post.domain.post.QInvite;
import org.example.pdnight.domain.post.domain.post.QPost;
import org.example.pdnight.domain.post.enums.AgeLimit;
import org.example.pdnight.domain.post.enums.Gender;
import org.example.pdnight.domain.post.enums.JoinStatus;
import org.example.pdnight.domain.post.enums.PostStatus;
import org.example.pdnight.domain.post.presentation.dto.response.InviteResponse;
import org.example.pdnight.domain.post.presentation.dto.response.PostResponse;
import org.example.pdnight.domain.post.presentation.dto.response.QInviteResponse;
import org.example.pdnight.domain.post.presentation.dto.response.QPostResponse;
import org.example.pdnight.global.common.enums.JobCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.example.pdnight.domain.post.domain.post.QPost.post;
import static org.example.pdnight.domain.post.domain.post.QPostLike.postLike;
import static org.example.pdnight.domain.post.domain.post.QPostParticipant.postParticipant;
import static org.example.pdnight.domain.post.domain.post.QPostTag.postTag;
import static org.example.pdnight.domain.post.domain.tag.QTag.tag;

@Repository
@RequiredArgsConstructor
public class PostReaderImpl implements PostReader {

    private final JPAQueryFactory queryFactory;

    // 게시글 단건 조회 + 참여자 목록
    @Override
    public Optional<Post> findByIdWithParticipants(Long postId) {
        Post findPost = queryFactory
                .select(post)
                .from(post)
                .leftJoin(post.postParticipants, postParticipant).fetchJoin()
                .where(post.id.eq(postId))
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

    public Page<PostResponse> getMyLikePost(Long userId, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder()
                .and(postLike.userId.eq(userId))
                .and(post.isDeleted.isFalse());

        // post 조회
        List<PostResponse> contents = queryFactory
                .select(new QPostResponse(
                                post.id,
                                post.authorId,
                                post.title,
                                post.timeSlot,
                                post.publicContent,
                                post.status,
                                post.maxParticipants,
                                post.genderLimit,
                                post.jobCategoryLimit,
                                post.ageLimit,
                                post.isFirstCome,
                                post.createdAt,
                                post.updatedAt
                        ))
                .from(post)
                .join(postLike).on(postLike.post.eq(post))
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 게시글 ID 리스트 추출
        List<Long> postIds = contents
                .stream()
                .map(PostResponse::getPostId)
                .toList();

        // tag 매핑
        Map<Long, List<String>> tagMap = getPostTagMap(postIds);
        for (PostResponse dto : contents) {
            List<String> hobbyList = tagMap.getOrDefault(dto.getPostId(), Collections.emptyList());
            dto.setTagList(hobbyList);
        }

        // total count
        Long total = Optional.ofNullable(
                queryFactory
                        .select(post.countDistinct())
                        .from(post)
                        .join(postLike).on(postLike.post.eq(post))
                        .where(builder)
                        .fetchOne()
        ).orElse(0L);

        return PageableExecutionUtils.getPage(contents, pageable, () -> total);
    }

    // 참여 신청한 게시글 목록 조회
    @Override
    public Page<PostResponse> getConfirmedPost(Long userId, JoinStatus joinStatus, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder()
                .and(postParticipant.userId.eq(userId))
                .and(post.isDeleted.isFalse());

        if (joinStatus != null) {
            builder.and(postParticipant.status.eq(joinStatus));
        }

        List<PostResponse> contents = queryFactory
                .select(new QPostResponse(
                        post.id,
                        post.authorId,
                        post.title,
                        post.timeSlot,
                        post.publicContent,
                        post.status,
                        post.maxParticipants,
                        post.genderLimit,
                        post.jobCategoryLimit,
                        post.ageLimit,
                        post.isFirstCome,
                        postParticipant.status,
                        postParticipant.createdAt,
                        post.createdAt,
                        post.updatedAt
                ))
                .from(post)
                .join(postParticipant).on(postParticipant.post.eq(post))
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 게시글 ID 리스트 추출
        List<Long> postIds = contents
                .stream()
                .map(PostResponse::getPostId)
                .toList();

        // tag 매핑
        Map<Long, List<String>> tagMap = getPostTagMap(postIds);
        for (PostResponse dto : contents) {
            List<String> hobbyList = tagMap.getOrDefault(dto.getPostId(), Collections.emptyList());
            dto.setTagList(hobbyList);
        }

        // total count
        Long total = Optional.ofNullable(
                queryFactory
                        .select(post.countDistinct())
                        .from(post)
                        .join(postParticipant).on(postParticipant.post.eq(post))
                        .where(builder)
                        .fetchOne()
        ).orElse(0L);

        return PageableExecutionUtils.getPage(contents, pageable, () -> total);
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
        builder.and(post.status.eq(PostStatus.OPEN));

        // 삭제 상태가 아닐 때
        builder.and(post.isDeleted.eq(false));

        // nullable 조건 추가
        if (maxParticipants != null) {
            builder.and(post.maxParticipants.goe(maxParticipants));
        }
        if (ageLimit != null) {
            builder.and(post.ageLimit.eq(ageLimit));
        }
        if (jobCategoryLimit != null) {
            builder.and(post.jobCategoryLimit.eq(jobCategoryLimit));
        }
        if (genderLimit != null) {
            builder.and(post.genderLimit.eq(genderLimit));
        }

        List<Post> contents = queryFactory
                .select(post)
                .from(post)
                .where(builder)
                .orderBy(post.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = Optional.ofNullable(
                queryFactory
                        .select(post.countDistinct())
                        .from(post)
                        .where(builder)
                        .fetchOne()
        ).orElse(0L);

        return new PageImpl<>(contents, pageable, total);
    }

    // 내가 작성 한 게시물 조회
    @Override
    public Page<Post> getWrittenPost(
            Long userId,
            Pageable pageable
    ) {
        BooleanBuilder builder = new BooleanBuilder();

        builder.and(post.authorId.eq(userId));
        // 삭제 상태가 아닐 때
        builder.and(post.isDeleted.eq(false));

        List<Post> contents = queryFactory
                .select(post)
                .from(post)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long count = queryFactory
                .select(post.count())
                .from(post)
                .where(builder)
                .fetchOne();

        return PageableExecutionUtils.getPage(contents, pageable, () -> Optional.ofNullable(count).orElse(0L));
    }

    // 추천 게시글 목록 조회
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
                        .and(post.isDeleted.eq(false)))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        JPQLQuery<Long> countQuery = queryFactory
                .select(invite.count())
                .from(invite)
                .where(invite.inviteeId.eq(userId));

        return PageableExecutionUtils.getPage(query.fetch(), pageable, countQuery::fetchOne);
    }

    // 내가 초대한 리스트
    @Override
    public Page<InviteResponse> getMyInvite(Long userId, Pageable pageable) {

        QInvite invite = QInvite.invite;

        JPQLQuery<InviteResponse> query = queryFactory
                .select(new QInviteResponse(
                        invite.id,
                        invite.inviteeId,
                        invite.post.id
                ))
                .from(invite)
                .where(invite.inviterId.eq(userId) // 내가 초대한 경우
                        .and(post.isDeleted.eq(false)))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        JPQLQuery<Long> countQuery = queryFactory
                .select(invite.count())
                .from(invite)
                .where(invite.inviterId.eq(userId));

        return PageableExecutionUtils.getPage(query.fetch(), pageable, countQuery::fetchOne);
    }
}
