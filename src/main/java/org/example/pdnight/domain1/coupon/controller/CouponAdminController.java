package org.example.pdnight.domain1.coupon.controller;


import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain1.common.dto.ApiResponse;
import org.example.pdnight.domain1.coupon.dto.request.CouponRequestDto;
import org.example.pdnight.domain1.coupon.dto.request.UpdateCouponRequestDto;
import org.example.pdnight.domain1.coupon.dto.response.CouponResponseDto;
import org.example.pdnight.domain1.coupon.service.CouponAdminService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/admin/coupons")
@RequiredArgsConstructor
public class CouponAdminController {
    private final CouponAdminService couponAdminService;

    // 쿠폰 생성
    @PostMapping
    public ResponseEntity<ApiResponse<CouponResponseDto>> createCoupon(@Validated @RequestBody CouponRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("쿠폰이 등록되었습니다.", couponAdminService.createCoupon(dto)));
    }

    // 쿠폰 조회
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CouponResponseDto>> getCoupon(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("쿠폰이 조회되었습니다.", couponAdminService.getCoupon(id)));
    }

    // 쿠폰 수정
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<CouponResponseDto>> updateCoupon(@PathVariable Long id, @RequestBody UpdateCouponRequestDto dto) {
        return ResponseEntity.ok(ApiResponse.ok("쿠폰이 수정되었습니다.", couponAdminService.updateCoupon(id, dto)));
    }

    // 쿠폰 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCoupon(@PathVariable Long id) {
        couponAdminService.deleteCoupon(id);
        return ResponseEntity.ok(ApiResponse.ok("쿠폰이 삭제되었습니다.", null));
    }
}
