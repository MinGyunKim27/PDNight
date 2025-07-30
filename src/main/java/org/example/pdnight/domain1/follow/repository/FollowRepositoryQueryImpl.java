package org.example.pdnight.domain1.follow.repository;

import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain1.follow.dto.response.FollowingResponseDto;
import org.example.pdnight.domain1.follow.dto.response.QFollowingResponseDto;
import org.example.pdnight.domain1.follow.entity.QFollow;
import org.example.pdnight.domain1.user.entity.QUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class FollowRepositoryQueryImpl implements FollowRepositoryQuery{
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<FollowingResponseDto> findFollowingsByUserId(Long userId, Pageable pageable) {
        QFollow follow = QFollow.follow;
        QUser user = QUser.user;

        JPQLQuery<FollowingResponseDto> query = queryFactory
                .select(new QFollowingResponseDto(
                        follow.following.id,
                        follow.following.nickname
                ))
                .from(follow)
                .join(follow.following, user)
                .where(follow.follower.id.eq(userId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        JPQLQuery<Long> countQuery = queryFactory
                .select(follow.count())
                .from(follow)
                .where(follow.follower.id.eq(userId));

        return PageableExecutionUtils.getPage(query.fetch(), pageable, countQuery::fetchOne);
    }

}
