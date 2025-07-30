package org.example.pdnight.domain.user.domain.couponDomain;

import org.example.pdnight.domain.user.domain.entity.UserCoupon;
import org.springframework.stereotype.Component;

@Component
public interface CouponReader {

    UserCoupon getCouponByCouponIdAndUserId(Long couponId, Long userId);
}
