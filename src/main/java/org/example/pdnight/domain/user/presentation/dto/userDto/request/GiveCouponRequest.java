package org.example.pdnight.domain.user.presentation.dto.userDto.request;

import lombok.Getter;

@Getter
public class GiveCouponRequest {
    Long userId;
    Long couponId;

    private GiveCouponRequest(Long userId,Long couponId){
        this.userId = userId;
        this.couponId = couponId;
    }

    public static GiveCouponRequest from(Long userId,Long couponId){
        return new GiveCouponRequest(userId,couponId);
    }
}
