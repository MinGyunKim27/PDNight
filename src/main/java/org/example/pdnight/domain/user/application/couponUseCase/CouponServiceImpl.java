package org.example.pdnight.domain.user.application.couponUseCase;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.user.presentation.dto.couponDto.request.CouponRequest;
import org.example.pdnight.domain.user.presentation.dto.couponDto.request.UpdateCouponRequest;
import org.example.pdnight.domain.user.presentation.dto.couponDto.response.CouponResponse;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {

    private final CouponCommanderService couponCommanderService;
    private final CouponReaderService couponReaderService;

    // --------------------- Admin Command Api ------------------------------------------------//
    // 쿠폰생성
    @Override
    public CouponResponse createCoupon(CouponRequest dto) {
        return couponCommanderService.createCoupon(dto);
    }

    // 쿠폰 수정
    @Override
    public CouponResponse updateCoupon(Long id, UpdateCouponRequest dto) {
        return couponCommanderService.updateCoupon(id, dto);
    }

    // 쿠폰삭제
    @Override
    public void deleteCoupon(Long id) {
        couponCommanderService.deleteCoupon(id);
    }
    // ---------------------- 조회 Api ---------------------------------------------------------//


    // --------------------- Admin 조회 Api ----------------------------------------------------//
    // 쿠폰 단건 조회
    @Override
    public CouponResponse getCoupon(Long id) {
        return couponReaderService.getCoupon(id);
    }
}
