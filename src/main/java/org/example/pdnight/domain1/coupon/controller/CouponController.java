package org.example.pdnight.domain1.coupon.controller;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain1.common.dto.ApiResponse;
import org.example.pdnight.domain1.coupon.dto.response.CouponResponseDto;
import org.example.pdnight.domain1.coupon.service.CouponService;
import org.example.pdnight.global.filter.CustomUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
public class CouponController {
    private final CouponService couponService;

    // 쿠폰사용
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<CouponResponseDto>> useCoupon(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        return ResponseEntity.ok(ApiResponse.ok("쿠폰을 사용하였습니다.", couponService.useCoupon(id, userDetails.getUserId())));
    }
}
