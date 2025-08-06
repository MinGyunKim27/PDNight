package org.example.pdnight.global.event;

public record CouponExpiredEvent(
        Long adminId,
        Long userId
) {
}
