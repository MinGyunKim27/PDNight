package org.example.pdnight.domain.user.application.couponUseCase;

import org.example.pdnight.domain.common.dto.PagedResponse;
import org.example.pdnight.domain.user.presentation.dto.couponDto.request.CouponRequestDto;
import org.example.pdnight.domain.user.presentation.dto.couponDto.request.UpdateCouponRequestDto;
import org.example.pdnight.domain.user.presentation.dto.couponDto.response.CouponResponseDto;
import org.springframework.data.domain.Pageable;

public interface CouponService {

    CouponResponseDto useCoupon(Long couponId, Long userId);

    CouponResponseDto updateCoupon(Long id, UpdateCouponRequestDto dto);

    void deleteCoupon(Long id);

    CouponResponseDto createCoupon(CouponRequestDto dto);

    PagedResponse<CouponResponseDto> getValidCoupons(Long userId, Pageable pageable);

    CouponResponseDto getCoupon(Long id);

}
