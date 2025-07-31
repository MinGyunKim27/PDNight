package org.example.pdnight.domain.user.infra.couponInfra;

import org.example.pdnight.domain.user.domain.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CouponJpaRepository extends JpaRepository<Coupon, Long> {
}
