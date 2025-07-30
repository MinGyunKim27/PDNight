package org.example.pdnight.domain.user.presentation.controller;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.common.dto.ApiResponse;
import org.example.pdnight.domain.common.dto.PagedResponse;
import org.example.pdnight.domain.user.presentation.dto.couponDto.request.CouponRequestDto;
import org.example.pdnight.domain.user.presentation.dto.couponDto.request.UpdateCouponRequestDto;
import org.example.pdnight.domain.user.presentation.dto.couponDto.response.CouponResponseDto;
import org.example.pdnight.domain.user.application.couponUseCase.CouponServiceImpl;
import org.example.pdnight.global.filter.CustomUserDetails;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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

    // 쿠폰사용
    @PatchMapping("/coupons/{id}")
    public ResponseEntity<ApiResponse<CouponResponseDto>> useCoupon(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        return ResponseEntity.ok(ApiResponse.ok("쿠폰을 사용하였습니다.", couponServiceImpl.useCoupon(id, userDetails.getUserId())));
    }

    // ----------------------------------------------------------------------------------------//
    // ----------------------------------------------------------------------------------------//
    // --------------------- Admin Command Api ------------------------------------------------//
    // ----------------------------------------------------------------------------------------//
    // ----------------------------------------------------------------------------------------//

    // 쿠폰 생성
    @PostMapping("/admin/coupons")
    public ResponseEntity<ApiResponse<CouponResponseDto>> createCoupon(@Validated @RequestBody CouponRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("쿠폰이 등록되었습니다.", couponServiceImpl.createCoupon(dto)));
    }

    // 쿠폰 수정
    @PatchMapping("/admin/coupons/{id}")
    public ResponseEntity<ApiResponse<CouponResponseDto>> updateCoupon(@PathVariable Long id, @RequestBody UpdateCouponRequestDto dto) {
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

    // 내 쿠폰목록 조회
    @GetMapping("/users/my/coupons")
    public ResponseEntity<ApiResponse<PagedResponse<CouponResponseDto>>> getMyCoupons(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(size = 10, page = 0) Pageable pageable
    ) {
        return ResponseEntity.ok(ApiResponse.ok("쿠폰이 조회되었습니다.", couponServiceImpl.getValidCoupons(userDetails.getUserId(), pageable)));
    }

    // ----------------------------------------------------------------------------------------//
    // ----------------------------------------------------------------------------------------//
    // --------------------- Admin 조회 Api ----------------------------------------------------//
    // ----------------------------------------------------------------------------------------//
    // ----------------------------------------------------------------------------------------//

    // 쿠폰 조회
    @GetMapping("/admin/coupons/{id}")
    public ResponseEntity<ApiResponse<CouponResponseDto>> getCoupon(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("쿠폰이 조회되었습니다.", couponServiceImpl.getCoupon(id)));
    }
}
