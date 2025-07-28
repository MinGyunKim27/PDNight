package org.example.pdnight.domain.coupon.service;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.common.enums.ErrorCode;
import org.example.pdnight.domain.common.exception.BaseException;
import org.example.pdnight.domain.common.helper.GetHelper;
import org.example.pdnight.domain.coupon.dto.request.CouponRequestDto;
import org.example.pdnight.domain.coupon.dto.request.UpdateCouponRequestDto;
import org.example.pdnight.domain.coupon.dto.response.CouponResponseDto;
import org.example.pdnight.domain.coupon.entity.Coupon;
import org.example.pdnight.domain.coupon.repository.CouponRepository;
import org.example.pdnight.domain.user.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CouponAdminService {

    private final CouponRepository couponRepository;
    private final GetHelper helper;

    // 쿠폰생성
    @Transactional
    public CouponResponseDto createCoupon(CouponRequestDto dto) {

        User user = helper.getUserByIdOrElseThrow(dto.getUserId());
        Coupon coupon = Coupon.create(user, dto.getCouponInfo(), dto.getDeadlineAt());


        couponRepository.save(coupon);
        user.addCoupon(coupon); // 양방향 연관관계 리스트에 쿠폰 추가
        return new CouponResponseDto(coupon);
    }

    // 쿠폰 단건 조회
    @Transactional(readOnly = true)
    public CouponResponseDto getCoupon(Long id) {
        Coupon coupon = getCouponById(id);
        return new CouponResponseDto(coupon);
    }

    // 쿠폰 수정
    @Transactional
    public CouponResponseDto updateCoupon(Long id, UpdateCouponRequestDto dto) {
        Coupon coupon = getCouponById(id);
        coupon.updateCoupon(dto);
        return new CouponResponseDto(coupon);
    }

    // 쿠폰삭제
    @Transactional
    public void deleteCoupon(Long id) {
        Coupon coupon = getCouponById(id);
        couponRepository.delete(coupon);
    }


    // ----------------------------------- HELPER 메서드 ------------------------------------------------------ //
    // get
    private Coupon getCouponById(Long id) {
        // 쿠폰 Id로 조회 없으면 예외
        return couponRepository.findById(id)
                .orElseThrow(() -> new BaseException(ErrorCode.COUPON_NOT_FOUND));
    }
}
