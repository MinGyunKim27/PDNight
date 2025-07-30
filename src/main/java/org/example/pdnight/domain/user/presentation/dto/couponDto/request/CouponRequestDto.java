package org.example.pdnight.domain.user.presentation.dto.couponDto.request;

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
