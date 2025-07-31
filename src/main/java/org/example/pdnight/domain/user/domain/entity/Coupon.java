package org.example.pdnight.domain.user.domain.entity;

import jakarta.persistence.*;
import lombok.*;
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

    private LocalDateTime defaultDeadlineAt;

    private Coupon(String couponInfo, LocalDateTime defaultDeadlineAt) {
        this.couponInfo = couponInfo;
        this.defaultDeadlineAt = defaultDeadlineAt;
    }

    public static Coupon create(String couponInfo, LocalDateTime deadlineAt) {
        return new Coupon(couponInfo, deadlineAt);
    }

    public void updateCoupon(String couponInfo, LocalDateTime defaultDeadlineAt) {
        if (couponInfo != null && !couponInfo.isEmpty()) {
            this.couponInfo = couponInfo;
        }
        if (defaultDeadlineAt != null) {
            this.defaultDeadlineAt = defaultDeadlineAt;
        }
    }
}