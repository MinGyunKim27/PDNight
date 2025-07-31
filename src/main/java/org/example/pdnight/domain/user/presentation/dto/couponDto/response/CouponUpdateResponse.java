package org.example.pdnight.domain.user.presentation.dto.couponDto.response;

import org.example.pdnight.domain.user.domain.entity.Coupon;

import java.time.LocalDateTime;

public class CouponUpdateResponse {
    private Long id;
    private String couponInfo;
    private LocalDateTime defaultDeadlineAt;

    protected CouponUpdateResponse(Coupon coupon){
        this.id = coupon.getId();
        this.couponInfo = coupon.getCouponInfo();
        this.defaultDeadlineAt = coupon.getDefaultDeadlineAt();
    }

    public static CouponUpdateResponse from(Coupon coupon){
        return new CouponUpdateResponse(coupon);
    }
}
