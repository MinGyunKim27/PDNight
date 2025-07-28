package org.example.pdnight.domain.coupon.service;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.common.dto.PagedResponse;
import org.example.pdnight.domain.common.enums.ErrorCode;
import org.example.pdnight.domain.common.exception.BaseException;
import org.example.pdnight.domain.coupon.dto.response.CouponResponseDto;
import org.example.pdnight.domain.coupon.entity.Coupon;
import org.example.pdnight.domain.coupon.repository.CouponRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;

    // 쿠폰사용
    @Transactional
    public CouponResponseDto useCoupon(Long couponId, Long userId) {
        Coupon coupon = getCouponById(couponId);

        validateUseCoupon(userId, coupon);

        coupon.use(); // 쿠폰 사용 처리
        return CouponResponseDto.from(coupon);
    }

    //  보유한 사용가능한 쿠폰 조회
    @Transactional(readOnly = true)
    public PagedResponse<CouponResponseDto> getValidCoupons(Long userId, Pageable pageable) {
        LocalDateTime now = LocalDateTime.now();
        return PagedResponse.from(couponRepository.findByUserIdAndIsUsedFalseAndValidDeadline(userId, now, pageable)
                .map(CouponResponseDto::from));
    }


    // ----------------------------------- HELPER 메서드 ------------------------------------------------------ //
    // get
    private Coupon getCouponById(Long couponId) {
        return couponRepository.findById(couponId)
                .orElseThrow(() -> new BaseException(ErrorCode.COUPON_NOT_FOUND));
    }

    // validate
    private void validateUseCoupon(Long userId, Coupon coupon) {
        if (!coupon.getUser().getId().equals(userId)) {
            throw new BaseException(ErrorCode.COUPON_FORBIDDEN); // 본인 쿠폰만 사용 가능
        }
    }

}
