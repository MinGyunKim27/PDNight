package org.example.pdnight.domain.coupon.dto.response;

import lombok.Getter;
import org.example.pdnight.domain.coupon.entity.Coupon;

import java.time.LocalDateTime;

@Getter
public class CouponResponseDto {
    private Long id;
    private Long userId;
    private String couponInfo;
    private boolean isUsed;
    private LocalDateTime deadlineAt;

    public CouponResponseDto(Coupon coupon) {
        this.userId = coupon.getUser().getId();
        this.couponInfo = coupon.getCouponInfo();
        this.isUsed = coupon.isUsed();
        this.deadlineAt = coupon.getDeadlineAt();
    }

    public static CouponResponseDto from(Coupon coupon) {
        return new CouponResponseDto(coupon);
    }
}
