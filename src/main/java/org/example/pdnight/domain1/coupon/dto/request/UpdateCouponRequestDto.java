package org.example.pdnight.domain1.coupon.dto.request;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class UpdateCouponRequestDto {
    private String couponInfo;
    private LocalDateTime deadlineAt;
}
