package org.example.pdnight.domain.post.repository;

import com.querydsl.core.BooleanBuilder;

import java.util.List;
import java.util.Optional;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.participant.entity.QPostParticipant;
import org.example.pdnight.domain.common.enums.JoinStatus;
import org.example.pdnight.domain.post.dto.response.PostResponseWithApplyStatusDto;
import org.example.pdnight.domain.post.dto.response.QPostResponseWithApplyStatusDto;
import org.example.pdnight.domain.post.entity.QPost;
import org.example.pdnight.domain.post.repository.QueryDslHelper.QuerydslExpressionHelper;
import org.example.pdnight.domain.postLike.entity.QPostLike;
import org.example.pdnight.domain.user.dto.response.PostWithJoinStatusAndAppliedAtResponseDto;
import org.example.pdnight.domain.user.dto.response.QPostWithJoinStatusAndAppliedAtResponseDto;
import org.springframework.data.support.PageableExecutionUtils;
import org.example.pdnight.domain.common.enums.JobCategory;
import org.example.pdnight.domain.post.enums.AgeLimit;
import org.example.pdnight.domain.post.enums.Gender;
import org.example.pdnight.domain.post.enums.PostStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PostRepositoryQueryImpl implements PostRepositoryQuery{
    private final JPAQueryFactory queryFactory;

	@Override
	public Page<PostResponseWithApplyStatusDto> getOpenedPosts(Long userId, Pageable pageable) {
		QPost post = QPost.post;

		List<PostResponseWithApplyStatusDto> results = queryFactory
				.select(new QPostResponseWithApplyStatusDto(
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
				))
				.from(post)
				.where(post.status.eq(PostStatus.OPEN)) // OPEN 상태 필터링
				.offset(pageable.getOffset())
				.limit(pageable.getPageSize())
				.fetch();

		Long count = queryFactory
				.select(post.count())
				.from(post)
				.where(post.status.eq(PostStatus.OPEN))
				.fetchOne();

		return PageableExecutionUtils.getPage(results, pageable, () -> Optional.ofNullable(count).orElse(0L));
	}

	@Override
	public PostResponseWithApplyStatusDto getOpenedPostById(Long postId) {
		QPost post = QPost.post;

		return queryFactory
				.select(new QPostResponseWithApplyStatusDto(
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
				))
				.from(post)
				.where(post.id.eq(postId)
						.and(post.status.eq(PostStatus.OPEN))) // OPEN 상태만 조회
				.fetchOne();
	}



	@Override
	public Page<PostResponseWithApplyStatusDto> getMyLikePost(Long userId, Pageable pageable) {
		QPost post = QPost.post;
		QPostLike postLike = QPostLike.postLike;

		BooleanBuilder builder = new BooleanBuilder();
		builder.and(post.status.ne(PostStatus.CLOSED));
		builder.and(postLike.user.id.eq(userId));

		List<PostResponseWithApplyStatusDto> content = queryFactory
				.select(new QPostResponseWithApplyStatusDto(
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
				))
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

		return PageableExecutionUtils.getPage(content, pageable, () -> Optional.ofNullable(count).orElse(0L));
	}


	@Override
    public Page<PostWithJoinStatusAndAppliedAtResponseDto> getConfirmedPost(Long userId,JoinStatus joinStatus,Pageable pageable) {
        QPost post = QPost.post;
        QPostParticipant postParticipant = QPostParticipant.postParticipant;

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
        builder.and(post.status.ne(PostStatus.CLOSED));

        if (joinStatus!=null){
            builder.and(postParticipant.status.eq(joinStatus));
        }


        List<PostWithJoinStatusAndAppliedAtResponseDto> content = queryFactory
                .select(qPostWithStatusResponseDto)
                .from(post)
                .join(postParticipant).on(postParticipant.post.eq(post))
                .where(
                        builder
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long count = queryFactory
                .select(post.count())
                .from(post)
                .join(postParticipant).on(postParticipant.post.eq(post))
                .where(
                        builder
                )
                .fetchOne();

        return PageableExecutionUtils.getPage(content, pageable, () -> Optional.ofNullable(count).orElse(0L));
    }


	@Override
	public Page<PostResponseWithApplyStatusDto> findPostDtosBySearch(
			Pageable pageable,
			Integer maxParticipants,
			AgeLimit ageLimit,
			JobCategory jobCategoryLimit,
			Gender genderLimit
	) {
		QPost post = QPost.post;

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
				.select(new QPostResponseWithApplyStatusDto(
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
				))
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


	@Override
	public Page<PostResponseWithApplyStatusDto> getWrittenPost(
			Long userId,
			Pageable pageable) {
		QPost post = QPost.post;

		BooleanBuilder builder = new BooleanBuilder();

		builder.and(post.author.id.eq(userId));
		builder.and(post.status.ne(PostStatus.CLOSED));

		List<PostResponseWithApplyStatusDto> writtenPost = queryFactory
				.select(new QPostResponseWithApplyStatusDto(
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
				))
				.from(post)
				.where(builder)
				.offset(pageable.getOffset())
				.limit(pageable.getPageSize())
				.fetch();

		Long count = queryFactory
				.select(post.count())
				.from(post)
				.where(
						builder
				)
				.fetchOne();

		return PageableExecutionUtils.getPage(writtenPost,pageable,() ->Optional.ofNullable(count).orElse(0L));
	}

	@Override
	public Page<PostResponseWithApplyStatusDto> getSuggestedPost(Long userId, Pageable pageable) {
		QPost post = QPost.post;
		QPostLike like = QPostLike.postLike;

		List<PostResponseWithApplyStatusDto> writtenPost = queryFactory
				.select(new QPostResponseWithApplyStatusDto(
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
				))
				.from(post)
				.leftJoin(like).on(like.post.eq(post)) // 좋아요 조인
				.groupBy(post.id)
				.where(like.user.id.eq(userId))
				.orderBy(like.count().desc()) // 좋아요 수 내림차순 정렬
				.offset(pageable.getOffset())
				.limit(pageable.getPageSize())
				.fetch();

		Long count = queryFactory
				.select(post.countDistinct())
				.from(post)
				.leftJoin(like).on(like.post.eq(post))
				.where(like.user.id.eq(userId))
				.fetchOne();

		return PageableExecutionUtils.getPage(writtenPost, pageable, () -> Optional.ofNullable(count).orElse(0L));
	}
}


