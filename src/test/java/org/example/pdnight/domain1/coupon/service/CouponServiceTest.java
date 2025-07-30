package org.example.pdnight.domain1.coupon.service;

import org.example.pdnight.domain1.common.enums.ErrorCode;
import org.example.pdnight.domain1.common.exception.BaseException;
import org.example.pdnight.domain1.coupon.dto.response.CouponResponseDto;
import org.example.pdnight.domain1.coupon.entity.Coupon;
import org.example.pdnight.domain1.coupon.repository.CouponRepository;
import org.example.pdnight.domain1.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CouponServiceTest {

    @Mock
    private CouponRepository couponRepository;

    @InjectMocks
    private CouponService couponService;

    @Test
    @DisplayName("쿠폰 사용 성공")
    void 쿠폰_사용_성공() {
        // given
        Long userId = 1L;
        Long couponId = 100L;
        User user = mock(User.class);
        Coupon coupon = mock(Coupon.class);

        // when
        when(user.getId()).thenReturn(userId);

        when(coupon.getUser()).thenReturn(user);
        when(couponRepository.findById(couponId)).thenReturn(Optional.of(coupon));
        CouponResponseDto result = couponService.useCoupon(couponId, userId);

        // then
        assertThat(result).isNotNull();
        verify(coupon).use();
    }

    @Test
    @DisplayName("쿠폰 사용 예외 - 쿠폰 없음")
    void 쿠폰_사용_쿠폰없음예외() {
        // given
        Long couponId = 100L;

        // when & then
        when(couponRepository.findById(couponId)).thenReturn(Optional.empty());
        BaseException exception = assertThrows(BaseException.class,
                () -> couponService.useCoupon(couponId, 1L));

        assertThat(exception.getMessage()).isEqualTo(ErrorCode.COUPON_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("쿠폰 사용 예외 - 다른 유저의 쿠폰")
    void 쿠폰_사용_다른유저쿠폰예외() {
        // given
        Long couponId = 100L;
        Long requestUserId = 2L;
        User user = mock(User.class);
        Coupon coupon = mock(Coupon.class);

        // when & then
        when(user.getId()).thenReturn(1L);
        when(coupon.getUser()).thenReturn(user);
        when(couponRepository.findById(couponId)).thenReturn(Optional.of(coupon));
        BaseException exception = assertThrows(BaseException.class,
                () -> couponService.useCoupon(couponId, requestUserId));

        assertThat(exception.getMessage()).isEqualTo(ErrorCode.COUPON_FORBIDDEN.getMessage());
    }

    @Test
    @DisplayName("쿠폰 조회 성공")
    void 쿠폰_조회_성공() {
        // given
        Long userId = 1L;
        User user = mock(User.class);
        Coupon coupon = mock(Coupon.class);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Coupon> page = new PageImpl<>(List.of(coupon));

        // when
        when(user.getId()).thenReturn(1L);
        when(coupon.getUser()).thenReturn(user);
        when(couponRepository.findByUserIdAndIsUsedFalseAndValidDeadline(eq(userId), any(), eq(pageable)))
                .thenReturn(page);
        couponService.getValidCoupons(userId, pageable);


        // then
        verify(couponRepository).findByUserIdAndIsUsedFalseAndValidDeadline(eq(userId), any(), eq(pageable));
    }
}
