package org.example.pdnight.domain.user.application.couponUseCase;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.common.dto.PagedResponse;
import org.example.pdnight.domain.common.enums.ErrorCode;
import org.example.pdnight.domain.common.exception.BaseException;
import org.example.pdnight.domain.common.helper.GetHelper;
import org.example.pdnight.domain.user.domain.entity.Coupon;
import org.example.pdnight.domain.user.infra.couponInfra.CouponJpaRepository;
import org.example.pdnight.domain.user.presentation.dto.couponDto.response.CouponResponseDto;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CouponQueryService {

    private final CouponJpaRepository couponJpaRepository;
    private final GetHelper helper;


    //  보유한 사용가능한 쿠폰 조회
    @Transactional(readOnly = true)
    public PagedResponse<CouponResponseDto> getValidCoupons(Long userId, Pageable pageable) {
        LocalDateTime now = LocalDateTime.now();
        return PagedResponse.from(couponJpaRepository.findByUserIdAndIsUsedFalseAndValidDeadline(userId, now, pageable)
                .map(CouponResponseDto::from));
    }

    // --------------------- Admin 조회 Api ----------------------------------------------------//
    // 쿠폰 단건 조회
    @Transactional(readOnly = true)
    public CouponResponseDto getCoupon(Long id) {
        Coupon coupon = getCouponById(id);
        return CouponResponseDto.from(coupon);
    }

    // ----------------------------------- HELPER 메서드 ------------------------------------------------------ //
    // get
    private Coupon getCouponById(Long couponId) {
        return couponJpaRepository.findById(couponId)
                .orElseThrow(() -> new BaseException(ErrorCode.COUPON_NOT_FOUND));
    }
}
