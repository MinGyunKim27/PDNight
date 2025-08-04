package org.example.pdnight.domain.user.application.couponUseCase;

import org.example.pdnight.domain.user.domain.couponDomain.CouponReader;
import org.example.pdnight.domain.user.domain.entity.Coupon;
import org.example.pdnight.domain.user.presentation.dto.couponDto.response.CouponResponse;
import org.example.pdnight.global.common.enums.ErrorCode;
import org.example.pdnight.global.common.exception.BaseException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CouponReaderServiceTest {

    @InjectMocks
    private CouponReaderService couponService;

    @Mock
    private CouponReader couponReader;

    @Test
    @DisplayName("쿠폰 조회 성공")
    void getCoupon() {
        // given
        Long couponId = 1L;

        // 결과물 생성
        String couponInfo = "쿠폰 정보";
        Integer defaultDeadlineDays = 5;
        Coupon coupon = Coupon.create(couponInfo, defaultDeadlineDays);

        when(couponReader.findById(couponId)).thenReturn(Optional.of(coupon));

        // when
        CouponResponse response = couponService.getCoupon(couponId);

        // then
        assertNotNull(response);
        assertEquals(couponInfo, response.getCouponInfo());
        assertEquals(defaultDeadlineDays, response.getDefaultDeadlineDays());
    }

    @Test
    @DisplayName("쿠폰 조회시 존재하지않는 쿠폰")
    void fail_getCoupon_getCouponById() {
        // given
        Long couponId = 1L;

        when(couponReader.findById(couponId)).thenReturn(Optional.empty());

        // when
        BaseException exception = assertThrows(BaseException.class, () ->
                couponService.getCoupon(couponId)
        );

        // then
        assertEquals(ErrorCode.COUPON_NOT_FOUND.getStatus(), exception.getStatus());
        assertEquals(ErrorCode.COUPON_NOT_FOUND.getMessage(), exception.getMessage());
    }
}
