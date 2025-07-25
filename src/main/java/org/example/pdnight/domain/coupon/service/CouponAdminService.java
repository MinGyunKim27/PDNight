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

    // 쿠폰사용
    @Transactional
    public CouponResponseDto createCoupon(CouponRequestDto dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));
        Coupon coupon = new Coupon(user, dto.getCouponInfo(), dto.getDeadlineAt());
        couponRepository.save(coupon);
        user.addCoupon(coupon); // 양방향 연관관계 리스트에 쿠폰 추가
        return new CouponResponseDto(coupon);
    }

    // 쿠폰 단건 조회
    @Transactional(readOnly = true)
    public CouponResponseDto getCoupon(Long id) {
        Coupon coupon = getCouponOrThrow(id);
        return new CouponResponseDto(coupon);
    }

    // 쿠폰 수정
    @Transactional
    public CouponResponseDto updateCoupon(Long id, UpdateCouponRequestDto dto) {
        Coupon coupon = getCouponOrThrow(id);

        coupon.updateCoupon(dto);


        return new CouponResponseDto(coupon);
    }

    // 쿠폰삭제
    @Transactional
    public void deleteCoupon(Long id) {
        Coupon coupon = getCouponOrThrow(id);

        couponRepository.delete(coupon);
    }

    // 쿠폰 Id로 조회 없으면 예외
    private Coupon getCouponOrThrow(Long id) {
        return couponRepository.findById(id)
                .orElseThrow(() -> new BaseException(ErrorCode.COUPON_NOT_FOUND));
    }
}
