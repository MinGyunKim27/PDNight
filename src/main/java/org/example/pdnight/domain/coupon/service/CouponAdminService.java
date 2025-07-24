package org.example.pdnight.domain.coupon.service;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.common.enums.ErrorCode;
import org.example.pdnight.domain.common.exception.BaseException;
import org.example.pdnight.domain.coupon.dto.request.CouponRequestDto;
import org.example.pdnight.domain.coupon.dto.request.UpdateCouponRequestDto;
import org.example.pdnight.domain.coupon.dto.response.CouponResponseDto;
import org.example.pdnight.domain.coupon.entity.Coupon;
import org.example.pdnight.domain.coupon.repository.CouponRepository;
import org.example.pdnight.domain.user.entity.User;
import org.example.pdnight.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CouponAdminService {
    private final CouponRepository couponRepository;
    private final UserRepository userRepository;

    @Transactional
    public CouponResponseDto createCoupon(CouponRequestDto dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));
        Coupon coupon = new Coupon(user, dto.getCouponInfo(), dto.getDeadlineAt());
        couponRepository.save(coupon);
        return new CouponResponseDto(coupon);
    }

    @Transactional(readOnly = true)
    public CouponResponseDto getCoupon(Long id) {
        Coupon coupon = getCouponOrThrow(id);
        return new CouponResponseDto(coupon);
    }

    @Transactional
    public CouponResponseDto updateCoupon(Long id, UpdateCouponRequestDto dto) {
        Coupon coupon = getCouponOrThrow(id);

        coupon.updateCoupon(dto);


        return new CouponResponseDto(coupon);
    }

    @Transactional
    public void deleteCoupon(Long id) {
        Coupon coupon = getCouponOrThrow(id);

        couponRepository.delete(coupon);
    }

    private Coupon getCouponOrThrow(Long id) {
        return couponRepository.findById(id)
                .orElseThrow(() -> new BaseException(ErrorCode.COUPON_NOT_FOUND));
    }
}
