package org.example.pdnight.domain.user.application.couponUseCase;

import org.example.pdnight.domain.user.domain.couponDomain.CouponCommander;
import org.example.pdnight.domain.user.domain.entity.Coupon;
import org.example.pdnight.domain.user.presentation.dto.couponDto.request.CouponRequest;
import org.example.pdnight.domain.user.presentation.dto.couponDto.request.UpdateCouponRequest;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CouponCommanderServiceTest {

    @InjectMocks
    private CouponCommanderService couponService;

    @Mock
    private CouponCommander couponCommander;


    @Test
    @DisplayName("쿠폰 생성 성공")
    void createCoupon() {
        // given
        String couponInfo = "쿠폰 정보";
        Integer defaultDeadlineDays = 5;
        CouponRequest request = mock(CouponRequest.class);
        when(request.getCouponInfo()).thenReturn(couponInfo);
        when(request.getDefaultDeadlineDays()).thenReturn(defaultDeadlineDays);

        Coupon coupon = Coupon.create(request.getCouponInfo(), request.getDefaultDeadlineDays());
        when(couponCommander.save(any(Coupon.class))).thenReturn(coupon);

        // when
        CouponResponse response = couponService.createCoupon(request);

        // then
        assertNotNull(response);
        assertEquals(couponInfo, response.getCouponInfo());
        assertEquals(defaultDeadlineDays, response.getDefaultDeadlineDays());
    }

    @Test
    @DisplayName("쿠폰 수정 성공")
    void updateCoupon() {
        // given
        Long couponId = 1L;

        String newInfo = "쿠폰 정보";
        Integer newDeadlineDays = 5;
        UpdateCouponRequest request = mock(UpdateCouponRequest.class);
        when(request.getCouponInfo()).thenReturn(newInfo);
        when(request.getDefaultDeadlineDays()).thenReturn(newDeadlineDays);

        // Coupon coupon = getCouponById(id);
        String couponInfo = "쿠폰 정보";
        Integer defaultDeadlineDays = 5;
        Coupon coupon = Coupon.create(couponInfo, defaultDeadlineDays);

        when(couponCommander.findById(couponId)).thenReturn(Optional.of(coupon));

        // coupon.updateCoupon(dto.getCouponInfo(), dto.getDefaultDeadlineDays());
        coupon.updateCoupon(request.getCouponInfo(), request.getDefaultDeadlineDays());


        // when
        CouponResponse response = couponService.updateCoupon(couponId, request);

        // then
        assertNotNull(response);
        assertEquals(newInfo, response.getCouponInfo());
        assertEquals(newDeadlineDays, response.getDefaultDeadlineDays());
    }

    @Test
    @DisplayName("쿠폰 삭제 성공")
    void deleteCoupon() {
        // given
        Long couponId = 1L;
        Coupon coupon = mock(Coupon.class);

        // Coupon coupon = getCouponById(id);
        when(couponCommander.findById(couponId)).thenReturn(Optional.of(coupon));

        // when
        couponService.deleteCoupon(couponId);

        // then
        verify(couponCommander).delete(coupon);
    }

    @Test
    @DisplayName("존재하지않는 쿠폰")
    void fail_updateCoupon_getCouponById() {
        // given
        Long couponId = 1L;
        UpdateCouponRequest request = mock(UpdateCouponRequest.class);

        // Coupon coupon = getCouponById(id);
        when(couponCommander.findById(couponId)).thenReturn(Optional.empty());

        // when
        BaseException exception = assertThrows(BaseException.class, () ->
                couponService.updateCoupon(couponId, request)
        );

        // then
        assertEquals(ErrorCode.COUPON_NOT_FOUND.getStatus(), exception.getStatus());
        assertEquals(ErrorCode.COUPON_NOT_FOUND.getMessage(), exception.getMessage());
    }
}
