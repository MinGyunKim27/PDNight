package org.example.pdnight.domain.user.presentation.dto.couponDto.response;

import lombok.Getter;
import org.example.pdnight.domain.user.domain.entity.Coupon;
import org.example.pdnight.domain.user.domain.entity.UserCoupon;

import java.time.LocalDateTime;

@Getter
public class CouponResponseDto {
    private Long id;
    private Long userId;
    private String couponInfo;
    private boolean isUsed;
    private LocalDateTime deadlineAt;

    protected CouponResponseDto(UserCoupon coupon) {
        this.userId = coupon.getUserId();
        this.couponInfo = coupon.getCoupon().getCouponInfo();
        this.isUsed = coupon.isUsed();
        this.deadlineAt = coupon.getCoupon().getDeadlineAt();
    }

    public static CouponResponseDto from(UserCoupon coupon) {
        return new CouponResponseDto(coupon);
    }
}
