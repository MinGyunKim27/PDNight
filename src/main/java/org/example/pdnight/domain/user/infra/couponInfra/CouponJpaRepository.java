package org.example.pdnight.domain.user.infra.couponInfra;

import org.example.pdnight.domain.user.domain.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponJpaRepository extends JpaRepository<Coupon, Long> {
}
