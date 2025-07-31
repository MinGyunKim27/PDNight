package org.example.pdnight.domain.user.application.couponUseCase;

import org.example.pdnight.domain.common.dto.PagedResponse;
import org.example.pdnight.domain.user.presentation.dto.couponDto.request.CouponRequest;
import org.example.pdnight.domain.user.presentation.dto.couponDto.request.UpdateCouponRequest;
import org.example.pdnight.domain.user.presentation.dto.couponDto.response.CouponResponse;
import org.example.pdnight.domain.user.presentation.dto.couponDto.response.CouponUpdateResponse;
import org.springframework.data.domain.Pageable;

public interface CouponService {

    CouponResponse createCoupon(CouponRequest dto);

    CouponUpdateResponse updateCoupon(Long id, UpdateCouponRequest dto);

    void deleteCoupon(Long id);

    PagedResponse<CouponResponse> getValidCoupons(Long userId, Pageable pageable);

    CouponResponse getCoupon(Long id);

}
