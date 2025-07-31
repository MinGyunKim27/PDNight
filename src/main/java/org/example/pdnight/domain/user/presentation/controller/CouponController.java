package org.example.pdnight.domain.user.presentation.controller;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.common.dto.ApiResponse;
import org.example.pdnight.domain.user.presentation.dto.couponDto.request.CouponRequest;
import org.example.pdnight.domain.user.presentation.dto.couponDto.request.UpdateCouponRequest;
import org.example.pdnight.domain.user.presentation.dto.couponDto.response.CouponResponse;
import org.example.pdnight.domain.user.application.couponUseCase.CouponServiceImpl;
import org.example.pdnight.domain.user.presentation.dto.couponDto.response.CouponUpdateResponse;
import org.example.pdnight.global.filter.CustomUserDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CouponController {
    private final CouponServiceImpl couponServiceImpl;

    // ----------------------------------------------------------------------------------------//
    // ----------------------------------------------------------------------------------------//
    // --------------------- Command Api ------------------------------------------------------//
    // ----------------------------------------------------------------------------------------//
    // ----------------------------------------------------------------------------------------//


    // ----------------------------------------------------------------------------------------//
    // ----------------------------------------------------------------------------------------//
    // --------------------- Admin Command Api ------------------------------------------------//
    // ----------------------------------------------------------------------------------------//
    // ----------------------------------------------------------------------------------------//

    // 쿠폰 생성
    @PostMapping("/admin/coupons")
    public ResponseEntity<ApiResponse<CouponResponse>> createCoupon(@Validated @RequestBody CouponRequest dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("쿠폰이 등록되었습니다.", couponServiceImpl.createCoupon(dto)));
    }

    // 쿠폰 수정
    @PatchMapping("/admin/coupons/{id}")
    public ResponseEntity<ApiResponse<CouponUpdateResponse>> updateCoupon(@PathVariable Long id, @RequestBody UpdateCouponRequest dto) {
        return ResponseEntity.ok(ApiResponse.ok("쿠폰이 수정되었습니다.", couponServiceImpl.updateCoupon(id, dto)));
    }

    // 쿠폰 삭제
    @DeleteMapping("/admin/coupons/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCoupon(@PathVariable Long id) {
        couponServiceImpl.deleteCoupon(id);
        return ResponseEntity.ok(ApiResponse.ok("쿠폰이 삭제되었습니다.", null));
    }

    // ----------------------------------------------------------------------------------------//
    // ----------------------------------------------------------------------------------------//
    // --------------------- 조회 Api ---------------------------------------------------------//
    // ----------------------------------------------------------------------------------------//
    // ----------------------------------------------------------------------------------------//

    // ----------------------------------------------------------------------------------------//
    // ----------------------------------------------------------------------------------------//
    // --------------------- Admin 조회 Api ----------------------------------------------------//
    // ----------------------------------------------------------------------------------------//
    // ----------------------------------------------------------------------------------------//

    // 쿠폰 조회
    @GetMapping("/admin/coupons/{id}")
    public ResponseEntity<ApiResponse<CouponResponse>> getCoupon(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("쿠폰이 조회되었습니다.", couponServiceImpl.getCoupon(id)));
    }
}
