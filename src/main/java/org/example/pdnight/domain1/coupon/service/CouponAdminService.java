package org.example.pdnight.domain1.coupon.service;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain1.common.enums.ErrorCode;
import org.example.pdnight.domain1.common.exception.BaseException;
import org.example.pdnight.domain1.common.helper.GetHelper;
import org.example.pdnight.domain1.coupon.dto.request.CouponRequestDto;
import org.example.pdnight.domain1.coupon.dto.request.UpdateCouponRequestDto;
import org.example.pdnight.domain1.coupon.dto.response.CouponResponseDto;
import org.example.pdnight.domain1.coupon.entity.Coupon;
import org.example.pdnight.domain1.coupon.repository.CouponRepository;
import org.example.pdnight.domain1.user.entity.User;
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
        return CouponResponseDto.from(coupon);
    }

    // 쿠폰 단건 조회
    @Transactional(readOnly = true)
    public CouponResponseDto getCoupon(Long id) {
        Coupon coupon = getCouponById(id);
        return CouponResponseDto.from(coupon);
    }

    // 쿠폰 수정
    @Transactional
    public CouponResponseDto updateCoupon(Long id, UpdateCouponRequestDto dto) {
        Coupon coupon = getCouponById(id);
        coupon.updateCoupon(dto.getCouponInfo(), dto.getDeadlineAt());
        return CouponResponseDto.from(coupon);
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
