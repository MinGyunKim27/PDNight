package org.example.pdnight.domain.coupon.service;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.common.enums.ErrorCode;
import org.example.pdnight.domain.common.exception.BaseException;
import org.example.pdnight.domain.coupon.dto.response.CouponResponseDto;
import org.example.pdnight.domain.coupon.entity.Coupon;
import org.example.pdnight.domain.coupon.repository.CouponRepository;
import org.example.pdnight.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;
    private final UserRepository userRepository;

    @Transactional
    public CouponResponseDto useCoupon(Long couponId, Long userId) {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new BaseException(ErrorCode.COUPON_NOT_FOUND));

        if (!coupon.getUser().getId().equals(userId)) {
            throw new BaseException(ErrorCode.COUPON_ALREADY_USED); // 본인 쿠폰만 사용 가능
        }

        coupon.use(); // 쿠폰 사용 처리
        return new CouponResponseDto(coupon);
    }
}
