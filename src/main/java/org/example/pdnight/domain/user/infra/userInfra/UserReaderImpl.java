package org.example.pdnight.domain.user.infra.userInfra;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.user.domain.entity.QFollow;
import org.example.pdnight.domain.user.domain.entity.QUser;
import org.example.pdnight.domain.user.domain.entity.User;
import org.example.pdnight.domain.user.domain.entity.UserCoupon;
import org.example.pdnight.domain.user.domain.userDomain.UserReader;
import org.example.pdnight.domain.user.presentation.dto.userDto.response.FollowingResponse;
import org.example.pdnight.domain.user.presentation.dto.userDto.response.QFollowingResponse;
import org.example.pdnight.domain.user.presentation.dto.userDto.response.UserCouponResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.example.pdnight.domain.user.domain.entity.QUserCoupon.userCoupon;
import static org.example.pdnight.domain.user.domain.entity.QCoupon.coupon;

@Repository
@RequiredArgsConstructor
public class UserReaderImpl implements UserReader {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<User> findById(Long id) {
        QUser user = QUser.user;

        return queryFactory
                .selectFrom(user)
                .where(user.id.eq(id))
                .stream()
                .findFirst();
    }

    @Override
    public Optional<User> findByIdWithFollow(Long id) {
        QUser user = QUser.user;

        return queryFactory
                .selectFrom(user)
                .leftJoin(user.followedOther)
                .where(user.id.eq(id))
                .distinct()
                .stream()
                .findFirst();
    }

    @Override
    public Page<User> searchUsers(String search, Pageable pageable) {
        QUser user = QUser.user;

        List<User> content = queryFactory
                .selectFrom(user)
                .where(
                        user.name.containsIgnoreCase(search)
                                .or(user.nickname.containsIgnoreCase(search)
                                        .or(user.name.containsIgnoreCase(search)))
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // Count 쿼리
        JPAQuery<Long> countQuery = queryFactory
                .select(user.count())
                .from(user)
                .where(
                        user.name.containsIgnoreCase(search)
                                .or(user.nickname.containsIgnoreCase(search))
                );

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }


    @Override
    public Optional<User> findByIdAndIsDeletedFalse(Long id) {

        QUser user = QUser.user;

        return queryFactory
                .selectFrom(user)
                .where(user.id.eq(id).and(user.isDeleted.eq(false)))
                .stream()
                .findFirst();
    }


    @Override
    public Page<User> findAll(Pageable pageable) {
        QUser user = QUser.user;

        List<User> users = queryFactory
                .selectFrom(user)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long count = queryFactory
                .select(user.count())
                .from(user)
                .fetchOne();


        return PageableExecutionUtils.getPage(users, pageable, () -> Optional.ofNullable(count).orElse(0L));
    }

    //있는데
    @Override
    public Page<FollowingResponse> findFollowingsByUserId(Long userId, Pageable pageable) {
        QFollow follow = QFollow.follow;
        QUser user = QUser.user;

        JPQLQuery<FollowingResponse> query = queryFactory
                .select(new QFollowingResponse(
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


    @Override
    public Page<UserCouponResponse> findUserCoupons(Long userId, LocalDateTime now, Pageable pageable) {
        List<UserCouponResponse> couponList = queryFactory
                .select(Projections.constructor(UserCouponResponse.class,
                        userCoupon,
                        coupon))
                .from(userCoupon)
                .join(coupon).on(userCoupon.couponId.eq(coupon.id))
                .where(
                        userCoupon.user.id.eq(userId),
                        userCoupon.isUsed.isFalse(),
                        userCoupon.deadlineAt.isNull()
                                .or(userCoupon.deadlineAt.after(now))
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPQLQuery<Long> total = queryFactory
                .select(userCoupon.count())
                .from(userCoupon)
                .where(
                        userCoupon.user.id.eq(userId),
                        userCoupon.isUsed.isFalse(),
                        userCoupon.deadlineAt.isNull()
                                .or(userCoupon.deadlineAt.after(now))
                );

        return PageableExecutionUtils.getPage(couponList, pageable, total::fetchOne);
    }

    @Override
    public Optional<UserCoupon> findUserCoupon(Long userId, Long couponId, LocalDateTime now) {
        return queryFactory
                .selectFrom(userCoupon)
                .where(
                        userCoupon.user.id.eq(userId),
                        userCoupon.id.eq(couponId),
                        userCoupon.isUsed.isFalse(),
                        userCoupon.deadlineAt.isNull()
                                .or(userCoupon.deadlineAt.after(now))
                )
                .stream()
                .findFirst();
    }
}
