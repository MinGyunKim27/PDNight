package org.example.pdnight.domain.user.application.userUseCase;

import org.example.pdnight.domain.user.domain.entity.User;
import org.example.pdnight.domain.user.domain.entity.UserCoupon;
import org.example.pdnight.domain.user.domain.userDomain.UserCommander;
import org.example.pdnight.domain.user.domain.userDomain.UserReader;
import org.example.pdnight.domain.user.infra.adaptor.UserCouponAdapter;
import org.example.pdnight.domain.user.presentation.dto.userDto.request.GiveCouponRequest;
import org.example.pdnight.domain.user.presentation.dto.userDto.request.UserNicknameUpdate;
import org.example.pdnight.domain.user.presentation.dto.userDto.request.UserUpdateRequest;
import org.example.pdnight.domain.user.presentation.dto.userDto.response.CouponInfo;
import org.example.pdnight.domain.user.presentation.dto.userDto.response.UserCouponResponse;
import org.example.pdnight.domain.user.presentation.dto.userDto.response.UserResponse;
import org.example.pdnight.global.common.enums.ErrorCode;
import org.example.pdnight.global.common.exception.BaseException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.example.pdnight.global.common.enums.ErrorCode.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserCommanderServiceTest {
    @Mock
    private UserReader userReader;
    @Mock
    private UserCommander userCommander;
    @Mock
    private UserInfoAssembler userInfoAssembler;
    @Mock
    private UserCouponAdapter userCouponAdapter;
    @InjectMocks
    private UserCommanderService userCommanderService;

    @Test
    void 프로필_업데이트_성공() {
        // given
        Long userId = 1L;
        List<String> hobbyNames = new ArrayList<>();
        hobbyNames.add("취미");
        List<String> techNames = new ArrayList<>();
        techNames.add("기술스택");
        UserUpdateRequest request = new UserUpdateRequest();
        ReflectionTestUtils.setField(request, "name", "홍길동");
        ReflectionTestUtils.setField(request, "nickname", "길동이");
        ReflectionTestUtils.setField(request, "gender", "남성");
        ReflectionTestUtils.setField(request, "age", 30L);
        ReflectionTestUtils.setField(request, "jobCategory", "개발자");
        ReflectionTestUtils.setField(request, "region", "서울");
        ReflectionTestUtils.setField(request, "comment", "안녕하세요");

        ReflectionTestUtils.setField(request, "hobbyIdList", List.of(1L));
        ReflectionTestUtils.setField(request, "techStackIdList", List.of(10L));
        User user = mock(User.class);
        UserResponse expectedResponse = UserResponse.from(user,hobbyNames,techNames); // 적절히 채움

        when(userReader.findById(userId)).thenReturn(Optional.ofNullable(user));
        when(userInfoAssembler.toDto(any(User.class))).thenReturn(expectedResponse);

        // when
        UserResponse result = userCommanderService.updateMyProfile(userId, request);

        // then
        verify(user).updateProfile(
                request.getName(),
                request.getNickname(),
                request.getGender(),
                request.getAge(),
                request.getJobCategory(),
                request.getRegion(),
                request.getComment(),
                request.getHobbyIdList(),
                request.getTechStackIdList()
        );
        verify(userCommander).save(user);
        assertEquals(expectedResponse, result);
    }

    @Test
    void 이름_수정_성공() {
        // given
        Long userId = 1L;
        List<String> hobbyNames = new ArrayList<>();
        hobbyNames.add("취미");
        List<String> techNames = new ArrayList<>();
        techNames.add("기술스택");
        UserNicknameUpdate request = new UserNicknameUpdate();
        ReflectionTestUtils.setField(request, "nickname", "길동이");

        User user = mock(User.class);
        UserResponse expectedResponse = UserResponse.from(user,hobbyNames,techNames); // 적절히 채움

        when(userReader.findById(userId)).thenReturn(Optional.ofNullable(user));
        when(userInfoAssembler.toDto(any(User.class))).thenReturn(expectedResponse);

        // when
        UserResponse result = userCommanderService.updateNickname(userId, request);

        // then
        verify(user).updateNickname(
                request.getNickname()
        );
        verify(userCommander).save(user);
        assertEquals(expectedResponse, result);
    }

    @Test
    void 회원_탈퇴_성공() {
        Long userId = 1L;
        User user = mock(User.class);
        ReflectionTestUtils.setField(user, "isDeleted", false);

        when(userReader.findById(userId)).thenReturn(Optional.of(user));

        userCommanderService.delete(userId);

        verify(user).softDelete();
    }

    @Test
    void 회원_탈퇴_실패() {
        Long userId = 1L;
        User user = mock(User.class);
        ReflectionTestUtils.setField(user, "isDeleted", true);
        when(user.getIsDeleted()).thenReturn(true);

        when(userReader.findById(userId)).thenReturn(Optional.of(user));

        BaseException exception = assertThrows(BaseException.class, () -> {
            userCommanderService.delete(userId);
        });

        assertThat(exception.getMessage()).isEqualTo("탈퇴 된 회원입니다.");
    }

    @Test
    void follow_자기자신팔로우_예외() {
        // given
        User user = mock(User.class);

        when(userReader.findByIdWithFollow(1L)).thenReturn(Optional.of(user));

        doThrow(new BaseException(INVALID_FOLLOW_SELF)).when(user)
                .validateIsSelfFollow(user, INVALID_FOLLOW_SELF);

        // when, then
        assertThrows(BaseException.class, () -> {
            userCommanderService.follow(1L, 1L);
        });
    }

    @Test
    void follow_중복팔로우_예외() {
        // given
        User user = mock(User.class);
        User user2 = mock(User.class);
        // 중복 팔로우 방지

        lenient().when(userReader.findByIdWithFollow(1L)).thenReturn(Optional.of(user));
        lenient().when(userReader.findByIdWithFollow(2L)).thenReturn(Optional.of(user2));

        lenient().doThrow(new BaseException(ALREADY_FOLLOWING)).when(user)
                .validateExistFollowing(user2);

        // when, then
        assertThrows(BaseException.class, () -> {
            userCommanderService.follow(2L, 1L);
        });
    }

    @Test
    void unFollow_자기자신언팔로우_예외() {
        // given
        User user = mock(User.class);

        when(userReader.findByIdWithFollow(1L)).thenReturn(Optional.of(user));

        doThrow(new BaseException(INVALID_UNFOLLOW_SELF)).when(user)
                .validateIsSelfFollow(user, INVALID_UNFOLLOW_SELF);

        // when, then
        assertThrows(BaseException.class, () -> {
            userCommanderService.unfollow(1L, 1L);
        });
    }

    @Test
    void unFollow_팔로우_중_아님_예외() {
        // given
        User user = mock(User.class);
        User user2 = mock(User.class);

        lenient().when(userReader.findByIdWithFollow(1L)).thenReturn(Optional.of(user));
        lenient().when(userReader.findByIdWithFollow(2L)).thenReturn(Optional.of(user2));

        lenient().doThrow(new BaseException(NOT_FOLLOWING)).when(user)
                .validateIsNotFollowing(user2,NOT_FOLLOWING);

        // when, then
        assertThrows(BaseException.class, () -> {
            userCommanderService.unfollow(2L, 1L);
        });
    }

    @Test
    void 쿠폰_부여_성공() {
        // given
        Long userId = 1L;
        Long couponId = 1L;
        int defaultDeadlineDays = 5;

        User user = mock(User.class);
        GiveCouponRequest request = GiveCouponRequest.from(userId, couponId);

        CouponInfo couponInfo = CouponInfo.from(defaultDeadlineDays);
        when(userReader.findById(userId)).thenReturn(Optional.ofNullable(user));
        when(userCouponAdapter.getCouponInfoById(couponId)).thenReturn(couponInfo);

        // userCoupon.create(...) 안에서 user.getId() 등을 쓸 수 있으므로
        when(user.getId()).thenReturn(userId);

        // when
        UserCouponResponse response = userCommanderService.giveCouponToUser(request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getCouponId()).isEqualTo(couponId);
        assertThat(response.getUserId()).isEqualTo(userId);

        // UserCoupon이 user에 추가되었는지 확인 (도메인 메서드 호출 여부)
        verify(user).addCoupon(any(UserCoupon.class));
    }

    @Test
    void 쿠폰_존재하지_않을_경우_예외() {
        // given
        Long userId = 1L;
        Long couponId = 999L;

        GiveCouponRequest request = GiveCouponRequest.from(userId, couponId);
        User user = mock(User.class);

        when(userReader.findById(userId)).thenReturn(Optional.of(user));

        // getCouponInfoById 호출 시 예외 던지기
        when(userCouponAdapter.getCouponInfoById(couponId))
                .thenThrow(new BaseException(ErrorCode.COUPON_NOT_FOUND));

        // when & then
        BaseException exception = assertThrows(BaseException.class, () -> {
            userCommanderService.giveCouponToUser(request);
        });

        assertThat(exception.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(exception.getMessage()).isEqualTo("존재하지 않는 쿠폰입니다.");
    }

    @Test
    void 쿠폰_사용_성공() {
        // given
        Long userId = 1L;
        Long couponId = 1L;

        UserCoupon userCoupon = mock(UserCoupon.class);
        when(userReader.findUserCoupon(eq(userId), eq(couponId), any(LocalDateTime.class)))
                .thenReturn(Optional.of(userCoupon));

        when(userCoupon.getUserId()).thenReturn(userId);
        when(userCoupon.getCouponId()).thenReturn(couponId);

        // when
        UserCouponResponse response = userCommanderService.useCoupon(couponId, userId);

        // then
        verify(userCoupon).use();
        assertThat(response).isNotNull();
        assertThat(response.getUserId()).isEqualTo(userId);
        assertThat(response.getCouponId()).isEqualTo(couponId);
    }

    @Test
    void 쿠폰_사용_실패_존재하지_않음() {
        // given
        Long userId = 1L;
        Long couponId = 99L;

        when(userReader.findUserCoupon(eq(userId), eq(couponId), any(LocalDateTime.class)))
                .thenReturn(Optional.empty());

        // when & then
        BaseException exception = assertThrows(BaseException.class, () -> {
            userCommanderService.useCoupon(couponId, userId);
        });

        assertThat(exception.getStatus()).isEqualTo(ErrorCode.COUPON_NOT_FOUND.getStatus());
        assertThat(exception.getMessage()).isEqualTo("존재하지 않는 쿠폰입니다."); // 메시지는 필요에 따라
    }
}
