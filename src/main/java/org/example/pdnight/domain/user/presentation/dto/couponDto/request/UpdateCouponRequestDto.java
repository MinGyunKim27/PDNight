package org.example.pdnight.domain.user.presentation.dto.couponDto.request;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class UpdateCouponRequestDto {
    private String couponInfo;
    private LocalDateTime deadlineAt;
}
