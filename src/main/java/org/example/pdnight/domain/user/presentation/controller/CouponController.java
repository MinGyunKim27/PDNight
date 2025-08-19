package org.example.pdnight.domain.user.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.user.application.couponUseCase.CouponService;
import org.example.pdnight.domain.user.presentation.dto.couponDto.request.CouponRequest;
import org.example.pdnight.domain.user.presentation.dto.couponDto.request.UpdateCouponRequest;
import org.example.pdnight.domain.user.presentation.dto.couponDto.response.CouponResponse;
import org.example.pdnight.global.common.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "AdminCoupon")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CouponController {
    private final CouponService couponService;

    // --------------------- Command Api ------------------------------------------------------//
    // --------------------- Admin Command Api ------------------------------------------------//

    // 쿠폰 생성
    @PostMapping("/admin/coupons")
    @Operation(summary = "[관리자] 쿠폰 생성", description = "관리자가 쿠폰을 등록합니다.")
    public ResponseEntity<ApiResponse<CouponResponse>> createCoupon(@Validated @RequestBody CouponRequest dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("쿠폰이 등록되었습니다.", couponService.createCoupon(dto)));
    }

    // 쿠폰 수정
    @PatchMapping("/admin/coupons/{id}")
    @Operation(summary = "[관리자] 쿠폰 수정", description = "관리자가 쿠폰을 수정합니다.")
    public ResponseEntity<ApiResponse<CouponResponse>> updateCoupon(@PathVariable Long id, @RequestBody UpdateCouponRequest dto) {
        return ResponseEntity.ok(ApiResponse.ok("쿠폰이 수정되었습니다.", couponService.updateCoupon(id, dto)));
    }

    // 쿠폰 삭제
    @DeleteMapping("/admin/coupons/{id}")
    @Operation(summary = "[관리자] 쿠폰 삭제", description = "관리자가 쿠폰을 삭제합니다.")
    public ResponseEntity<ApiResponse<Void>> deleteCoupon(@PathVariable Long id) {
        couponService.deleteCoupon(id);
        return ResponseEntity.ok(ApiResponse.ok("쿠폰이 삭제되었습니다.", null));
    }

    // --------------------- 조회 Api ---------------------------------------------------------//
    // --------------------- Admin 조회 Api ----------------------------------------------------//

    // 쿠폰 조회
    @GetMapping("/admin/coupons/{id}")
    @Operation(summary = "[관리자] 쿠폰 조회", description = "관리자가 쿠폰을 조회합니다.")
    public ResponseEntity<ApiResponse<CouponResponse>> getCoupon(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("쿠폰이 조회되었습니다.", couponService.getCoupon(id)));
    }
}
