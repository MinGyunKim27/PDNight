package org.example.pdnight.domain.user.presentation.dto.couponDto.response;

import lombok.Getter;
import org.example.pdnight.domain.user.domain.entity.UserCoupon;
import org.example.pdnight.domain.user.domain.entity.Coupon;
import java.time.LocalDateTime;

@Getter
public class CouponResponse {
    private Long id;
    private Long userId;
    private Coupon coupon;
    private boolean isUsed;
    private LocalDateTime deadlineAt;

    protected CouponResponse(UserCoupon userCoupon, Coupon coupon) {
        this.userId = userCoupon.getUserId();
        this.coupon = coupon;
        this.isUsed = userCoupon.isUsed();
        this.deadlineAt = userCoupon.getDeadlineAt();
    }

    public static CouponResponse from(UserCoupon userCoupon, Coupon coupon) {
        return new CouponResponse(userCoupon, coupon);
    }
}
