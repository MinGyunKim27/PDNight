package org.example.pdnight.domain.user.infra.userInfra;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.user.application.port.UserCouponPort;
import org.example.pdnight.domain.user.domain.couponDomain.CouponReader;
import org.example.pdnight.domain.user.domain.entity.Coupon;
import org.example.pdnight.domain.user.presentation.dto.userDto.response.CouponInfo;
import org.example.pdnight.global.common.enums.ErrorCode;
import org.example.pdnight.global.common.exception.BaseException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserCouponAdapter implements UserCouponPort {
    private final CouponReader couponReader;

    @Override
    public CouponInfo getCouponInfoById(Long couponId) {
        Coupon coupon = couponReader.findById(couponId).orElseThrow(
                () -> new BaseException(ErrorCode.COUPON_NOT_FOUND)
        );
        return CouponInfo.from(coupon.getDefaultDeadlineDays());
    }
}
