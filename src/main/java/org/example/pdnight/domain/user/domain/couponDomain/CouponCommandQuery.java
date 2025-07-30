package org.example.pdnight.domain.user.domain.couponDomain;

import org.example.pdnight.domain.user.domain.entity.Coupon;
import org.springframework.stereotype.Component;

@Component
public interface CouponCommandQuery {

    void save(Coupon coupon);
}
