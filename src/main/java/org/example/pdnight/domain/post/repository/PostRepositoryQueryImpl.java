package org.example.pdnight.domain.post.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.common.enums.JobCategory;
import org.example.pdnight.domain.common.enums.JoinStatus;
import org.example.pdnight.domain.post.dto.response.PostResponseWithApplyStatusDto;
import org.example.pdnight.domain.post.dto.response.PostWithJoinStatusAndAppliedAtResponseDto;
import org.example.pdnight.domain.post.dto.response.QPostResponseWithApplyStatusDto;
import org.example.pdnight.domain.post.dto.response.QPostWithJoinStatusAndAppliedAtResponseDto;
import org.example.pdnight.domain.post.entity.Post;
import org.example.pdnight.domain.post.enums.AgeLimit;
import org.example.pdnight.domain.post.enums.Gender;
import org.example.pdnight.domain.post.enums.PostStatus;
import org.example.pdnight.domain.post.repository.QueryDslHelper.QuerydslExpressionHelper;
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
import static org.example.pdnight.domain.participant.entity.QPostParticipant.postParticipant;
import static org.example.pdnight.domain.post.entity.QPost.post;
import static org.example.pdnight.domain.postLike.entity.QPostLike.postLike;

@Repository
@RequiredArgsConstructor
public class PostRepositoryQueryImpl implements PostRepositoryQuery {
    private final JPAQueryFactory queryFactory;

    // 게시글 단건 조회
    @Override
    public PostResponseWithApplyStatusDto getOpenedPostById(Long postId) {
        PostResponseWithApplyStatusDto content = queryFactory
                .select(postResponseDtoProjection())
                .from(post)
                .where(post.id.eq(postId)
                        .and(post.status.eq(PostStatus.OPEN))) // OPEN 상태만 조회
                .fetchOne();

        // null 이면 추가 조회 막기
        if (content == null) return null;

        return content;
    }

    // 닫힌 상태가 아닌 게시글 단건 조회
    @Override
    public Optional<Post> getPostByIdNotClose(Long postId) {

        Post findPost = queryFactory
                .select(post)
                .from(post)
                .where(post.id.eq(postId)
                        .and(post.status.ne(PostStatus.CLOSED))) // OPEN 상태만 조회
                .fetchOne();

        return Optional.ofNullable(findPost);
    }


    // 내 좋아요 게시글 목록 조회
    @Override
    public Page<PostResponseWithApplyStatusDto> getMyLikePost(Long userId, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();
        //닫힌 상태가 아닐 때
        builder.and(post.status.ne(PostStatus.CLOSED));
        builder.and(postLike.user.id.eq(userId));

        List<PostResponseWithApplyStatusDto> contents = queryFactory
                .select(postResponseDtoProjection())
                .from(post)
                .join(postLike).on(postLike.post.eq(post))
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long count = queryFactory
                .select(post.count())
                .from(post)
                .join(postLike).on(postLike.post.eq(post))
                .where(builder)
                .fetchOne();

        return PageableExecutionUtils.getPage(contents, pageable, () -> Optional.ofNullable(count).orElse(0L));
    }


    // 참여 신청한 게시글 목록 조회
    @Override
    public Page<PostWithJoinStatusAndAppliedAtResponseDto> getConfirmedPost(Long userId, JoinStatus joinStatus, Pageable pageable) {
        QPostWithJoinStatusAndAppliedAtResponseDto qPostWithStatusResponseDto = new QPostWithJoinStatusAndAppliedAtResponseDto(
                post.id,
                post.author.id,
                post.title,
                post.timeSlot,
                post.publicContent,
                post.privateContent,
                post.status,
                post.maxParticipants,
                post.genderLimit,
                post.jobCategoryLimit,
                post.ageLimit,
                postParticipant.status,
                postParticipant.createdAt);

        BooleanBuilder builder = new BooleanBuilder();

        builder.and(postParticipant.user.id.eq(userId));
        //닫힌 상태가 아닐 때
        builder.and(post.status.ne(PostStatus.CLOSED));

        if (joinStatus != null) {
            builder.and(postParticipant.status.eq(joinStatus));
        }


        List<PostWithJoinStatusAndAppliedAtResponseDto> content = queryFactory
                .select(qPostWithStatusResponseDto)
                .from(post)
                .join(postParticipant).on(postParticipant.post.eq(post))
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long count = queryFactory
                .select(post.count())
                .from(post)
                .join(postParticipant).on(postParticipant.post.eq(post))
                .where(builder)
                .fetchOne();

        return PageableExecutionUtils.getPage(content, pageable, () -> Optional.ofNullable(count).orElse(0L));
    }


    // 게시글 검색 목록 조회
    @Override
    public Page<PostResponseWithApplyStatusDto> findPostDtosBySearch(
            Pageable pageable,
            Integer maxParticipants,
            AgeLimit ageLimit,
            JobCategory jobCategoryLimit,
            Gender genderLimit,
            List<Long> hobbyIds,
            List<Long> techStackIds
    ) {
        // 조건 누적용 BooleanBuilder
        BooleanBuilder builder = new BooleanBuilder();
        // 무조건 포함되는 조건
        builder.and(post.status.eq(PostStatus.OPEN));
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

        List<PostResponseWithApplyStatusDto> contents = queryFactory
                .select(postResponseDtoProjection())
                .from(post)
                .leftJoin(post.author)
                .where(builder)
                .groupBy(post.id)
                .orderBy(post.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();


        Long total = Optional.ofNullable(
                queryFactory
                        .select(post.countDistinct())
                        .from(post)
                        .leftJoin(post.author)
                        .where(builder)
                        .fetchOne()
        ).orElse(0L);

        return new PageImpl<>(contents, pageable, total);
    }


    // 내가 작성 한 게시물 조회
    @Override
    public Page<PostResponseWithApplyStatusDto> getWrittenPost(
            Long userId,
            Pageable pageable
    ) {
        BooleanBuilder builder = new BooleanBuilder();

        builder.and(post.author.id.eq(userId));
        builder.and(post.status.ne(PostStatus.CLOSED));

        List<PostResponseWithApplyStatusDto> contents = queryFactory
                .select(postResponseDtoProjection())
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
    public Page<PostResponseWithApplyStatusDto> getSuggestedPost(Long userId, Pageable pageable) {
        List<PostResponseWithApplyStatusDto> contents = queryFactory
                .select(postResponseDtoProjection())
                .from(post)
                .leftJoin(postLike).on(postLike.post.eq(post)) // 좋아요 조인
                .groupBy(post.id)
                .where(postLike.user.id.eq(userId).and(post.status.eq(PostStatus.OPEN)))
                .orderBy(postLike.count().desc()) // 좋아요 수 내림차순 정렬
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();


        Long count = queryFactory
                .select(post.countDistinct())
                .from(post)
                .leftJoin(postLike).on(postLike.post.eq(post))
                .where(postLike.user.id.eq(userId))
                .fetchOne();

        return PageableExecutionUtils.getPage(contents, pageable, () -> Optional.ofNullable(count).orElse(0L));
    }

    // -- HELPER 메서드 -- //

    // DTO 프로젝션 : 참여자 수, 신청자 수
    private QPostResponseWithApplyStatusDto postResponseDtoProjection() {
        return new QPostResponseWithApplyStatusDto(
                post.id,
                post.author.id,
                post.title,
                post.timeSlot,
                post.publicContent,
                post.privateContent,
                post.status,
                post.maxParticipants,
                post.genderLimit,
                post.jobCategoryLimit,
                post.ageLimit,
                QuerydslExpressionHelper.participantCount(post),
                QuerydslExpressionHelper.acceptedParticipantCount(post),
                post.createdAt,
                post.updatedAt
        );
    }

}
