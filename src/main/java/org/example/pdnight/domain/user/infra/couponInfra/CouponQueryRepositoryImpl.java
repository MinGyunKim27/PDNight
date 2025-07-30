package org.example.pdnight.domain.user.infra.couponInfra;

import java.util.List;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.user.domain.entity.Coupon;
import org.example.pdnight.domain.user.domain.entity.UserCoupon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import static org.example.pdnight.domain.user.domain.entity.QCoupon.coupon;
import static org.example.pdnight.domain.user.domain.entity.QUser.user;
import static org.example.pdnight.domain.user.domain.entity.QUserCoupon.userCoupon;

@Repository
@RequiredArgsConstructor
public class CouponQueryRepositoryImpl {

    private final JPAQueryFactory queryFactory;

//    @Query("SELECT c FROM Coupon c WHERE c.user.id = :userId AND c.isUsed = false " +
//            "AND (c.deadlineAt IS NULL OR c.deadlineAt > :now)")
    public Page<Coupon> findByUserIdAndIsUsedFalseAndValidDeadline(@Param("userId") Long userId,
                                                            @Param("now") LocalDateTime now,
                                                            Pageable pageable){
        List<Coupon> couponList = queryFactory.select(coupon)
                .from(coupon)
                .where(
                        coupon.user.id.eq(userId),
                        coupon.isUsed.isFalse(),
                        coupon.deadlineAt.isNull()
                                .or(coupon.deadlineAt.after(now))
                        )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory
                .select(coupon.count())
                .from(coupon)
                .where(
                        coupon.user.id.eq(userId),
                        coupon.isUsed.isFalse(),
                        coupon.deadlineAt.isNull()
                                .or(coupon.deadlineAt.after(now))
                )
                .fetchOne();

        return new PageImpl<>(couponList, pageable, total);
    }

    public UserCoupon getCouponByCouponIdAndUserId(Long couponId, Long userId) {
        return queryFactory.select(userCoupon)
                .from(userCoupon)
                .where(userCoupon.couponId.eq(couponId),
                        userCoupon.userId.eq(userId))
                .fetchOne();

    }
}
