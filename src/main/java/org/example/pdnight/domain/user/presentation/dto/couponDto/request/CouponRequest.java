package org.example.pdnight.domain.user.presentation.dto.couponDto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class CouponRequest {
    @Schema(description = "쿠폰 정보", example = "couponInfo")
    @NotNull
    private String couponInfo;

    @Schema(description = "쿠폰 유효 기간", example = "20")
    private Integer defaultDeadlineDays;
}
