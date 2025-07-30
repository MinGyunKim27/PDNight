package org.example.pdnight.domain1.coupon.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CouponRequestDto {
    @NotNull
    private Long userId;
    @NotNull
    private String couponInfo;
    private LocalDateTime deadlineAt;
}
