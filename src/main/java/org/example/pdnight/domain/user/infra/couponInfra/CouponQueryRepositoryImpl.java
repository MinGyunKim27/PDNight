package org.example.pdnight.domain.user.infra.couponInfra;

import java.util.List;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.user.domain.couponDomain.CouponReader;
import org.example.pdnight.domain.user.domain.entity.Coupon;
import org.example.pdnight.domain.user.domain.entity.UserCoupon;
import org.example.pdnight.domain.user.presentation.dto.couponDto.response.CouponResponse;
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
public class CouponQueryRepositoryImpl implements CouponReader {

    private final JPAQueryFactory queryFactory;

    public Coupon getCouponByCouponIdAndUserId(Long couponId) {
        return queryFactory.select(coupon)
                .from(coupon)
                .where(coupon.id.eq(couponId))
                .fetchOne();
    }
}
