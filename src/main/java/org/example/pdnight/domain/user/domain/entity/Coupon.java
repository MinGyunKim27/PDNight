package org.example.pdnight.domain.user.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    private Integer defaultDeadlineDays;

    private Coupon(String couponInfo, Integer defaultDeadlineDays) {
        this.couponInfo = couponInfo;
        this.defaultDeadlineDays = defaultDeadlineDays;
    }

    public static Coupon create(String couponInfo, Integer defaultDeadlineDays) {
        return new Coupon(couponInfo, defaultDeadlineDays);
    }

    public void updateCoupon(String couponInfo, Integer defaultDeadlineDays) {
        if (couponInfo != null && !couponInfo.isEmpty()) {
            this.couponInfo = couponInfo;
        }
        if (defaultDeadlineDays != null) {
            this.defaultDeadlineDays = defaultDeadlineDays;
        }
    }
}
