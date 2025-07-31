package org.example.pdnight.domain.user.application.couponUseCase;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.common.dto.PagedResponse;
import org.example.pdnight.domain.common.enums.ErrorCode;
import org.example.pdnight.domain.common.exception.BaseException;
import org.example.pdnight.domain.common.helper.GetHelper;
import org.example.pdnight.domain.user.domain.couponDomain.CouponReader;
import org.example.pdnight.domain.user.domain.entity.Coupon;
import org.example.pdnight.domain.user.infra.couponInfra.CouponJpaRepository;
import org.example.pdnight.domain.user.presentation.dto.couponDto.response.CouponResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CouponQueryService {

    private final CouponReader couponReader;
    private final GetHelper helper;


    // --------------------- Admin 조회 Api ----------------------------------------------------//
    // 쿠폰 단건 조회
    @Transactional(readOnly = true)
    public CouponResponse getCoupon(Long id) {
        Coupon coupon = getCouponById(id);
        return CouponResponse.from(coupon);
    }

    // ----------------------------------- HELPER 메서드 ------------------------------------------------------ //
    // get
    private Coupon getCouponById(Long couponId) {
        return couponReader.findById(couponId)
                .orElseThrow(() -> new BaseException(ErrorCode.COUPON_NOT_FOUND));
    }
}
