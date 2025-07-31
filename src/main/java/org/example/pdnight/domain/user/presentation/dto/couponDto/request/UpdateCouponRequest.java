package org.example.pdnight.domain.user.presentation.dto.couponDto.request;

import lombok.Getter;

@Getter
public class UpdateCouponRequest {
    private String couponInfo;
    private Integer defaultDeadlineDays;
}
