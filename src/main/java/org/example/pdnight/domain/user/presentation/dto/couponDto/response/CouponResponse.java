package org.example.pdnight.domain.user.presentation.dto.couponDto.response;

import org.example.pdnight.domain.user.domain.entity.Coupon;

public class CouponResponse {
    private Long id;
    private String couponInfo;
    private Integer defaultDeadlineDays;

    protected CouponResponse(Coupon coupon) {
        this.id = coupon.getId();
        this.couponInfo = coupon.getCouponInfo();
        this.defaultDeadlineDays = coupon.getDefaultDeadlineDays();
    }

    public static CouponResponse from(Coupon coupon) {
        return new CouponResponse(coupon);
    }
}
