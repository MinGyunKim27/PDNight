package org.example.pdnight.domain.user.application.couponUseCase;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.common.dto.PagedResponse;
import org.example.pdnight.domain.user.presentation.dto.couponDto.request.CouponRequestDto;
import org.example.pdnight.domain.user.presentation.dto.couponDto.request.UpdateCouponRequestDto;
import org.example.pdnight.domain.user.presentation.dto.couponDto.response.CouponResponseDto;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {

    private final CouponCommandService couponCommandService;
    private final CouponQueryService couponQueryService;

    // ---------------------- Command Api -----------------------------------------------------//
    // 쿠폰사용
    @Override
    public CouponResponseDto useCoupon(Long couponId, Long userId) {
        return couponCommandService.useCoupon(couponId, userId);
    }

    // --------------------- Admin Command Api ------------------------------------------------//
    // 쿠폰 수정
    @Override
    public CouponResponseDto updateCoupon(Long id, UpdateCouponRequestDto dto) {
        return couponCommandService.updateCoupon(id, dto);
    }

    // 쿠폰삭제
    @Override
    public void deleteCoupon(Long id) {
        couponCommandService.deleteCoupon(id);
    }

    // 쿠폰생성
    @Override
    public CouponResponseDto createCoupon(CouponRequestDto dto) {
        return couponCommandService.createCoupon(dto);
    }

    // ---------------------- 조회 Api ---------------------------------------------------------//
    //  보유한 사용가능한 쿠폰 조회
    @Override
    public PagedResponse<CouponResponseDto> getValidCoupons(Long userId, Pageable pageable) {
        return couponQueryService.getValidCoupons(userId, pageable);
    }

    // --------------------- Admin 조회 Api ----------------------------------------------------//
    // 쿠폰 단건 조회
    @Override
    public CouponResponseDto getCoupon(Long id) {
        return couponQueryService.getCoupon(id);
    }
}
