package org.example.pdnight.domain.user.application.couponUseCase;

import org.example.pdnight.domain.user.presentation.dto.couponDto.request.CouponRequest;
import org.example.pdnight.domain.user.presentation.dto.couponDto.request.UpdateCouponRequest;
import org.example.pdnight.domain.user.presentation.dto.couponDto.response.CouponResponse;

public interface CouponService {

    CouponResponse createCoupon(CouponRequest dto);

    CouponResponse updateCoupon(Long id, UpdateCouponRequest dto);

    void deleteCoupon(Long id);

    CouponResponse getCoupon(Long id);

}
