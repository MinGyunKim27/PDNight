package org.example.pdnight.domain.user.presentation.dto.couponDto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class CouponRequest {
    @NotNull
    private Long couponId;
    @NotNull
    private String couponInfo;
    private Integer defaultDeadlineDays;
}
