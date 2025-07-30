package org.example.pdnight.domain.user.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.pdnight.domain.common.enums.ErrorCode;
import org.example.pdnight.domain.common.exception.BaseException;

import java.time.LocalDateTime;


@Entity
@Table(name = "coupons")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String couponInfo;

    private LocalDateTime deadlineAt;

    private Coupon(String couponInfo, LocalDateTime deadlineAt) {
        this.couponInfo = couponInfo;
        this.deadlineAt = deadlineAt;
    }

    public static Coupon create(String couponInfo, LocalDateTime deadlineAt) {
        return new Coupon(couponInfo, deadlineAt);
    }

    public void updateCoupon(String couponInfo, LocalDateTime deadlineAt) {
        if (couponInfo != null && !couponInfo.isEmpty()) {
            this.couponInfo = couponInfo;
        }
        if (deadlineAt != null) {
            this.deadlineAt = deadlineAt;
        }
    }
}
