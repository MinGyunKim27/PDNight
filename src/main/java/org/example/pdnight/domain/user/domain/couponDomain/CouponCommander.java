package org.example.pdnight.domain.user.domain.couponDomain;

import org.example.pdnight.domain.user.domain.entity.Coupon;

public interface CouponCommander {

    Coupon save(Coupon coupon);

    void delete(Coupon coupon);
}
