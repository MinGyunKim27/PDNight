package org.example.pdnight.domain.coupon.repository;

import org.example.pdnight.domain.coupon.entity.Coupon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface CouponRepository extends JpaRepository<Coupon, Long> {
    @Query("SELECT c FROM Coupon c WHERE c.user.id = :userId AND c.isUsed = false " +
            "AND (c.deadlineAt IS NULL OR c.deadlineAt > :now)")
    Page<Coupon> findByUserIdAndIsUsedFalseAndValidDeadline(@Param("userId") Long userId,
                                                            @Param("now") LocalDateTime now,
                                                            Pageable pageable);
}
