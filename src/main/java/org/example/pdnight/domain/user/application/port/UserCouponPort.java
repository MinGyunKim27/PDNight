package org.example.pdnight.domain.user.application.port;

import org.example.pdnight.domain.user.presentation.dto.userDto.response.CouponInfo;

public interface UserCouponPort {
    CouponInfo getCouponInfoById(Long couponId);
}
