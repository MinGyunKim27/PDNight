package org.example.pdnight.domain.user.domain.couponDomain;

import org.example.pdnight.domain.user.domain.entity.Coupon;

import java.util.Optional;

public interface CouponReader {

    Optional<Coupon> findById(Long couponId);

}
