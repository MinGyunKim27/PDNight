package org.example.pdnight.domain.user.application.couponUseCase;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.common.enums.ErrorCode;
import org.example.pdnight.domain.common.exception.BaseException;
import org.example.pdnight.domain.user.domain.couponDomain.CouponCommander;
import org.example.pdnight.domain.user.domain.couponDomain.CouponReader;
import org.example.pdnight.domain.user.domain.entity.Coupon;
import org.example.pdnight.domain.user.presentation.dto.couponDto.request.CouponRequest;
import org.example.pdnight.domain.user.presentation.dto.couponDto.request.UpdateCouponRequest;
import org.example.pdnight.domain.user.presentation.dto.couponDto.response.CouponResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CouponCommanderService {

    private final CouponCommander couponCommander;
    private final CouponReader couponReader;

    // --------------------- Admin Api ------------------------------------------------//
    // 쿠폰생성
    @Transactional
    public CouponResponse createCoupon(CouponRequest dto) {
        Coupon coupon = Coupon.create(dto.getCouponInfo(), dto.getDefaultDeadlineDays());
        couponCommander.save(coupon);
        return CouponResponse.from(coupon);
    }

    // 쿠폰 수정
    @Transactional
    public CouponResponse updateCoupon(Long id, UpdateCouponRequest dto) {
        Coupon coupon = getCouponById(id);
        coupon.updateCoupon(dto.getCouponInfo(), dto.getDefaultDeadlineDays());
        return CouponResponse.from(coupon);
    }

    // 쿠폰삭제
    @Transactional
    public void deleteCoupon(Long id) {
        Coupon coupon = getCouponById(id);
        couponCommander.delete(coupon);
    }

    // ----------------------------------- HELPER 메서드 ------------------------------------------------------ //
    // get
    private Coupon getCouponById(Long couponId) {
        return couponReader.findById(couponId).orElseThrow(
                () -> new BaseException(ErrorCode.COUPON_NOT_FOUND)
        );
    }
}
