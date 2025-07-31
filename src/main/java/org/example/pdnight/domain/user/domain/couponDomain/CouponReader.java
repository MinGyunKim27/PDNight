package org.example.pdnight.domain.user.domain.couponDomain;

import aj.org.objectweb.asm.commons.Remapper;
import org.example.pdnight.domain.user.domain.entity.Coupon;
import org.example.pdnight.domain.user.domain.entity.UserCoupon;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

public interface CouponReader {
    Coupon getCouponByCouponIdAndUserId(Long couponId);
}
