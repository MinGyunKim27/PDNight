package org.example.pdnight.global.event;

import java.util.List;

public record CouponExpiredEvent(
        List<Long> userIds
) {
}
