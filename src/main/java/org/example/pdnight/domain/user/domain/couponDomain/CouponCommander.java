package org.example.pdnight.domain.user.domain.couponDomain;

import org.example.pdnight.domain.user.domain.entity.Coupon;

import java.util.Optional;

public interface CouponCommander {

    Coupon save(Coupon coupon);

    void delete(Coupon coupon);

    Optional<Coupon> findById(Long couponId);

}