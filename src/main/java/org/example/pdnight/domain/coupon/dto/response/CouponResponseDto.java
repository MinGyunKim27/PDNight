package org.example.pdnight.domain.coupon.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class CouponResponseDto {
    private Long id;
    private Long userId;
    private String couponInfo;
    private boolean isUsed;
    private LocalDateTime deadlineAt;
}
