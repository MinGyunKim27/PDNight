package org.example.pdnight.domain.user.infra.couponInfra;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.user.domain.couponDomain.CouponCommander;
import org.example.pdnight.domain.user.domain.entity.Coupon;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CouponCommanderImpl implements CouponCommander {
    private final CouponJpaRepository couponJpaRepository;

    @Override
    public Coupon save(Coupon coupon){
        return couponJpaRepository.save(coupon);
    }

    @Override
    public void delete(Coupon coupon){
        couponJpaRepository.delete(coupon);
    }

    @Override
    public Optional<Coupon> findById(Long couponId) {
        return couponJpaRepository.findById(couponId);
    };

}