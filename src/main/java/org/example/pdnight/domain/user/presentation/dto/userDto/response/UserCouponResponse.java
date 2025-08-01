package org.example.pdnight.domain.user.presentation.dto.userDto.response;

import lombok.Getter;
import org.example.pdnight.domain.user.domain.entity.UserCoupon;

import java.time.LocalDateTime;

@Getter
public class UserCouponResponse {
    private Long userId;
    private Long couponId;
    private boolean isUsed;
    private LocalDateTime deadlineAt;

    public UserCouponResponse(UserCoupon userCoupon) {
        this.userId = userCoupon.getUserId();
        this.couponId = userCoupon.getCouponId();
        this.isUsed = userCoupon.isUsed();
        this.deadlineAt = userCoupon.getDeadlineAt();
    }

    public static UserCouponResponse from(UserCoupon userCoupon) {
        return new UserCouponResponse(userCoupon);
    }
}
