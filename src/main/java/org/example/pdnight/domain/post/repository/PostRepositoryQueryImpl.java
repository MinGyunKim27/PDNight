package org.example.pdnight.domain.post.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.common.enums.JobCategory;
import org.example.pdnight.domain.participant.entity.QPostParticipant;
import org.example.pdnight.domain.participant.enums.JoinStatus;
import org.example.pdnight.domain.post.dto.response.PostResponseDto;
import org.example.pdnight.domain.post.entity.Post;
import org.example.pdnight.domain.post.entity.QPost;
import org.example.pdnight.domain.post.enums.AgeLimit;
import org.example.pdnight.domain.post.enums.Gender;
import org.example.pdnight.domain.post.enums.PostStatus;
import org.example.pdnight.domain.postLike.entity.QPostLike;
import org.example.pdnight.domain.user.dto.response.PostWithJoinStatusAndAppliedAtResponseDto;
import org.example.pdnight.domain.user.dto.response.QPostWithJoinStatusAndAppliedAtResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static org.example.pdnight.domain.hobby.entity.QPostHobby.postHobby;
import static org.example.pdnight.domain.post.entity.QPost.post;
import static org.example.pdnight.domain.techStack.entity.QPostTech.postTech;

@Repository
@RequiredArgsConstructor
public class PostRepositoryQueryImpl implements PostRepositoryQuery {
    private final JPAQueryFactory queryFactory;

    public Page<Post> getMyLikePost(Long userId, Pageable pageable) {
        QPost post = QPost.post;
        QPostLike postLike = QPostLike.postLike;

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(post.status.ne(PostStatus.CLOSED));
        builder.and(postLike.user.id.eq(userId));

        List<Post> content = queryFactory
                .select(post)
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
                .where(postLike.user.id.eq(userId))
                .fetchOne();

        return PageableExecutionUtils.getPage(content, pageable, () -> Optional.ofNullable(count).orElse(0L));
    }

    @Override
    public Page<PostWithJoinStatusAndAppliedAtResponseDto> getConfirmedPost(Long userId, JoinStatus joinStatus, Pageable pageable) {
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

        if (joinStatus != null) {
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
    public Page<PostResponseDto> findPostDtosBySearch(
            Pageable pageable,
            Integer maxParticipants,
            AgeLimit ageLimit,
            JobCategory jobCategoryLimit,
            Gender genderLimit
    ) {
        // 페이징 정보 따로 조회 : 조인이랑 같이하면 페이징 안됨
        List<Long> postIds = queryFactory
                .select(post.id)
                .from(post)
                .where(
                        post.maxParticipants.goe(maxParticipants),
                        post.status.eq(PostStatus.OPEN),
                        ageLimitEq(ageLimit),
                        jobCategoryLimitEq(jobCategoryLimit),
                        genderLimitEq(genderLimit)
                )
                .orderBy(post.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 조인으로 연관 엔티티 한꺼번에 조회
        List<Post> posts = queryFactory
                .selectFrom(post)
                .leftJoin(post.author).fetchJoin()
                .leftJoin(post.postHobbies, postHobby).fetchJoin()
                .leftJoin(postHobby.hobby).fetchJoin()
                .leftJoin(post.postTechs, postTech).fetchJoin()
                .leftJoin(postTech.techStack).fetchJoin()
                .where(post.id.in(postIds))
                .orderBy(post.createdAt.desc())
                .distinct()
                .fetch();

        List<PostResponseDto> contents = posts.stream()
                .map(PostResponseDto::new)
                .toList();

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

    @Override
    public Page<PostResponseDto> getWrittenPost(
            Long userId,
            Pageable pageable) {
        QPost post1 = post;

        BooleanBuilder builder = new BooleanBuilder();

        builder.and(post1.author.id.eq(userId));
        builder.and(post1.status.ne(PostStatus.CLOSED));

        List<PostResponseDto> writtenPost = queryFactory.select(Projections.constructor(
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
                        post.updatedAt))
                .from(post1)
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

        return PageableExecutionUtils.getPage(writtenPost, pageable, () -> Optional.ofNullable(count).orElse(0L));
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
