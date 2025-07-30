package org.example.pdnight.domain.user.infra.couponInfra;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.user.domain.couponDomain.CouponCommandQuery;
import org.example.pdnight.domain.user.domain.entity.Coupon;

@RequiredArgsConstructor
public class CouponRepositoryImpl implements CouponCommandQuery {
    private final CouponJpaRepository couponJpaRepository;

    public Coupon Save(Coupon coupon){
        return couponJpaRepository.save(coupon);
    }
}
