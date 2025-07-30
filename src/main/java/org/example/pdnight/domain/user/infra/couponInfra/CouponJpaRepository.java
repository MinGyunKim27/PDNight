package org.example.pdnight.domain.user.infra.couponInfra;

import org.example.pdnight.domain.user.domain.entity.Coupon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface CouponJpaRepository extends JpaRepository<Coupon, Long> {

}
