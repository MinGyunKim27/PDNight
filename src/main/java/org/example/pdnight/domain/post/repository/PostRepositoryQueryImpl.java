package org.example.pdnight.domain.post.repository;

import static org.example.pdnight.domain.post.entity.QPost.*;

import java.util.List;
import java.util.Optional;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

import org.example.pdnight.domain.common.enums.JobCategory;
import org.example.pdnight.domain.post.dto.response.PostResponseDto;
import org.example.pdnight.domain.post.enums.AgeLimit;
import org.example.pdnight.domain.post.enums.Gender;
import org.example.pdnight.domain.post.enums.PostStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PostRepositoryQueryImpl implements PostRepositoryQuery {
	private final JPAQueryFactory queryFactory;

	@Override
	public Page<PostResponseDto> findPostDtosBySearch(
		Pageable pageable,
		Integer maxParticipants,
		AgeLimit ageLimit,
		JobCategory jobCategoryLimit,
		Gender genderLimit
	) {
		List<PostResponseDto> contents = queryFactory
			.select(
				Projections.constructor(
					PostResponseDto.class,
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
					post.createdAt,
					post.updatedAt
				))
			.from(post)
			.leftJoin(post.author)
			.where(
				post.maxParticipants.goe(maxParticipants),
				post.status.eq(PostStatus.OPEN),
				ageLimitEq(ageLimit),
				jobCategoryLimitEq(jobCategoryLimit),
				genderLimitEq(genderLimit)
			)
			.groupBy(post.id)
			.orderBy(post.createdAt.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		//페이징 용 카운팅 쿼리
		Long total = Optional.ofNullable(
			queryFactory
				.select(post.id.countDistinct())
				.from(post)
				.leftJoin(post.author)
				.where(
					post.maxParticipants.goe(maxParticipants),
					post.status.eq(PostStatus.OPEN),
					ageLimitEq(ageLimit),
					jobCategoryLimitEq(jobCategoryLimit),
					genderLimitEq(genderLimit)
				)
				.fetchOne()
		).orElse(0L);

		return new PageImpl<>(contents, pageable, total);
	}

	//이하 헬퍼메서드
	private BooleanExpression ageLimitEq(AgeLimit ageLimit) {
		return ageLimit != null ? post.ageLimit.eq(ageLimit) : null;
	}

	private BooleanExpression jobCategoryLimitEq(JobCategory jobCategoryLimit) {
		return jobCategoryLimit != null ? post.jobCategoryLimit.eq(jobCategoryLimit) : null;
	}

	private BooleanExpression genderLimitEq(Gender genderLimit) {
		return genderLimit != null ? post.genderLimit.eq(genderLimit) : null;
	}

}
