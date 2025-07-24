package org.example.pdnight.domain.coupon.repository;

import org.example.pdnight.domain.coupon.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponRepository extends JpaRepository<Coupon, Long> {
}
