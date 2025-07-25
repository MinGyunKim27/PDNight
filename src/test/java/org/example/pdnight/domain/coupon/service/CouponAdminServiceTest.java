package org.example.pdnight.domain.coupon.service;

import org.example.pdnight.domain.common.enums.ErrorCode;
import org.example.pdnight.domain.common.exception.BaseException;
import org.example.pdnight.domain.coupon.dto.request.CouponRequestDto;
import org.example.pdnight.domain.coupon.dto.request.UpdateCouponRequestDto;
import org.example.pdnight.domain.coupon.dto.response.CouponResponseDto;
import org.example.pdnight.domain.coupon.entity.Coupon;
import org.example.pdnight.domain.coupon.repository.CouponRepository;
import org.example.pdnight.domain.user.entity.User;
import org.example.pdnight.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CouponAdminServiceTest {

    @Mock
    private CouponRepository couponRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CouponAdminService couponAdminService;

    @Test
    @DisplayName("쿠폰 생성 성공")
    void 쿠폰을_생성_성공() {
        // given
        Long userId = 1L;
        CouponRequestDto dto = mock(CouponRequestDto.class);
        User user = mock(User.class);
        Coupon coupon = new Coupon(user, dto.getCouponInfo(), dto.getDeadlineAt());

        // when
        when(dto.getUserId()).thenReturn(userId);
        when(dto.getCouponInfo()).thenReturn("쿠폰");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(couponRepository.save(any(Coupon.class))).thenReturn(coupon);


        CouponResponseDto result = couponAdminService.createCoupon(dto);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getCouponInfo()).isEqualTo("쿠폰");
        verify(user).addCoupon(any(Coupon.class));
        verify(couponRepository).save(any(Coupon.class));
    }

    @Test
    @DisplayName("쿠폰생성시 유저없음 예외 발생")
    void 쿠폰_생성시_유저없음_예외() {
        // given
        CouponRequestDto dto = mock(CouponRequestDto.class);

        // when & then
        when(dto.getUserId()).thenReturn(1L);
        when(userRepository.findById(dto.getUserId())).thenReturn(Optional.empty());

        BaseException exception = assertThrows(BaseException.class, () -> couponAdminService.createCoupon(dto));
        assertThat(exception.getMessage()).isEqualTo(ErrorCode.USER_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("쿠폰 조회 성공")
    void 쿠폰을_조회_성공() {
        // given
        Long couponId = 1L;
        Coupon coupon = mock(Coupon.class);
        User user = mock(User.class);

        // when
        when(user.getId()).thenReturn(1L);
        when(coupon.getUser()).thenReturn(user);
        when(couponRepository.findById(couponId)).thenReturn(Optional.of(coupon));
        CouponResponseDto result = couponAdminService.getCoupon(couponId);

        // then
        assertThat(result).isNotNull();
        verify(couponRepository).findById(couponId);
    }

    @Test
    @DisplayName("쿠폰 조회시 존재하지않는 쿠폰")
    void 쿠폰_조회시_존재하지않는_쿠폰() {
        // given

        // when & then
        when(couponRepository.findById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> couponAdminService.getCoupon(1L))
                .isInstanceOf(BaseException.class)
                .hasMessage(ErrorCode.COUPON_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("쿠폰 수정 성공")
    void 쿠폰_수정_성공() {
        // given
        Long couponId = 1L;
        Coupon coupon = mock(Coupon.class);
        UpdateCouponRequestDto dto = mock(UpdateCouponRequestDto.class);
        User user = mock(User.class);

        // when
        when(user.getId()).thenReturn(1L);
        when(coupon.getUser()).thenReturn(user);
        when(couponRepository.findById(couponId)).thenReturn(Optional.of(coupon));
        CouponResponseDto result = couponAdminService.updateCoupon(couponId, dto);

        // then
        assertThat(result).isNotNull();
        verify(coupon).updateCoupon(dto);
    }

    @Test
    @DisplayName("쿠폰 수정시 존재하지않는 쿠폰")
    void 쿠폰_수정시_존재하지않는_쿠폰() {
        // given

        // when & then
        when(couponRepository.findById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> couponAdminService.updateCoupon(1L, mock(UpdateCouponRequestDto.class)))
                .isInstanceOf(BaseException.class)
                .hasMessage(ErrorCode.COUPON_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("쿠폰 삭제 성공")
    void 쿠폰_삭제_성공() {
        // given
        Long couponId = 1L;
        Coupon coupon = mock(Coupon.class);

        // when
        when(couponRepository.findById(couponId)).thenReturn(Optional.of(coupon));
        couponAdminService.deleteCoupon(couponId);

        // then
        verify(couponRepository).delete(coupon);
    }

    @Test
    @DisplayName("쿠폰 삭제시 존재하지않는 쿠폰")
    void 쿠폰_삭제시_존재하지않는_쿠폰() {
        // given

        // when & then
        when(couponRepository.findById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> couponAdminService.deleteCoupon(1L))
                .isInstanceOf(BaseException.class)
                .hasMessage(ErrorCode.COUPON_NOT_FOUND.getMessage());
    }
}
