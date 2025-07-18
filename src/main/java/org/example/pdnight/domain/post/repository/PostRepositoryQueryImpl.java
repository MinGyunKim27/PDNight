package org.example.pdnight.domain.post.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.participant.entity.QPostParticipant;
import org.example.pdnight.domain.participant.enums.JoinStatus;
import org.example.pdnight.domain.post.entity.Post;
import org.example.pdnight.domain.post.entity.QPost;
import org.example.pdnight.domain.post.enums.PostStatus;
import org.example.pdnight.domain.post.service.PostService;
import org.example.pdnight.domain.postLike.entity.QPostLike;
import org.example.pdnight.domain.user.dto.response.PostWithJoinStatusAndAppliedAtResponseDto;
import org.example.pdnight.domain.user.dto.response.QPostWithJoinStatusAndAppliedAtResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PostRepositoryQueryImpl implements PostRepositoryQuery{
    private final JPAQueryFactory queryFactory;

    public Page<Post> getMyLikePost(Long userId, Pageable pageable){
        QPost post = QPost.post;
        QPostLike postLike = QPostLike.postLike;

        List<Post> content = queryFactory
                .select(post)
                .from(post)
                .join(postLike).on(postLike.post.eq(post))
                .where(postLike.user.id.eq(userId))
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
}
