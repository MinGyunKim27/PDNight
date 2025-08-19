package org.example.pdnight.domain.user.presentation.dto.userDto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class GiveCouponRequest {
    @Schema(description = "부여할 사용자 ID", example = "2")
    Long userId;

    @Schema(description = "부여할 쿠폰 ID", example = "1")
    Long couponId;

    private GiveCouponRequest(Long userId,Long couponId){
        this.userId = userId;
        this.couponId = couponId;
    }

    public static GiveCouponRequest from(Long userId,Long couponId){
        return new GiveCouponRequest(userId,couponId);
    }
}
