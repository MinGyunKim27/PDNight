package org.example.pdnight.domain.follow.service;

import org.example.pdnight.domain.common.enums.ErrorCode;
import org.example.pdnight.domain.common.exception.BaseException;
import org.example.pdnight.domain.common.helper.GetHelper;
import org.example.pdnight.domain.user.application.userUseCase.UserService;
import org.example.pdnight.domain.user.domain.entity.Follow;
import org.example.pdnight.domain.user.domain.entity.User;
import org.example.pdnight.domain.user.domain.userDomain.UserCommander;
import org.example.pdnight.domain.user.domain.userDomain.UserReader;
import org.example.pdnight.domain.user.presentation.dto.userDto.response.FollowResponse;
import org.example.pdnight.domain.user.presentation.dto.userDto.response.FollowingResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FollowServiceTest {
    private UserReader userReader;
    private UserCommander userCommandQuery;
    private GetHelper helper;

    @InjectMocks
    private UserService userService;

    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        userReader = mock(UserReader.class);
        userCommandQuery = mock(UserCommander.class);

        user1 = Mockito.mock(User.class);
        lenient().when(user1.getId()).thenReturn(1L);
        user2 = Mockito.mock(User.class);
        lenient().when(user2.getId()).thenReturn(2L);
    }

    @Test
    @DisplayName("성공적으로 팔로우를 수행한다")
    void follow_success() {
        when(userReader.findById(1L)).thenReturn(Optional.of(user1));
        when(userReader.findById(2L)).thenReturn(Optional.of(user2));

        FollowResponse result = userService.follow(1L, 2L);

        assertThat(result).isNotNull();
        verify(user1).addFollow(eq(user2), any(Follow.class));
    }

    @Test
    @DisplayName("자기 자신을 팔로우하면 예외가 발생한다")
    void follow_self_fail() {
        BaseException exception = assertThrows(BaseException.class, () ->
                userService.follow(1L, 1L)
        );
        assertThat(exception.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("이미 팔로우한 사용자에게 팔로우 시도 시 예외 발생")
    void follow_duplicate_fail() {
        // given
        Follow follow = Follow.create(user1, user2); // user1 -> user2

        // 실제로 user1이 user2를 이미 팔로우한 상태로 세팅
        user1.getFollowingList().add(follow);
        user2.getFollowList().add(follow);

        when(userReader.findById(1L)).thenReturn(Optional.of(user1)); // 로그인 유저
        when(userReader.findById(2L)).thenReturn(Optional.of(user2)); // 팔로우 대상

        // when & then
        BaseException exception = assertThrows(BaseException.class, () ->
                userService.follow(2L, 1L) // userId = 대상, loginId = 로그인 유저
        );

        assertThat(exception.getStatus()).isEqualTo(ErrorCode.ALREADY_FOLLOWING.getStatus());
        assertThat(exception.getMessage()).isEqualTo(ErrorCode.ALREADY_FOLLOWING.getMessage());
    }


    @Test
    @DisplayName("언팔로우 성공")
    void unfollow_success() {
        // given
        when(userReader.findById(1L)).thenReturn(Optional.of(user1)); // loginId
        when(userReader.findById(2L)).thenReturn(Optional.of(user2)); // userId (언팔할 대상)

        // when
        userService.unfollow(2L, 1L); // userId, loginId 순

        // then
        verify(user1).unfollow(user2);
    }

    @Test
    @DisplayName("언팔로우: 자기 자신을 언팔 시도할 경우 예외 발생")
    void unfollow_self_fail() {
        BaseException exception = assertThrows(BaseException.class, () ->
                userService.unfollow(1L, 1L)
        );
        assertThat(exception.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("언팔로우: 팔로우 중이 아니면 예외 발생")
    void unfollow_not_following_fail() {
        // given
        when(userReader.findById(1L)).thenReturn(Optional.of(user1)); // 로그인 유저
        when(userReader.findById(2L)).thenReturn(Optional.of(user2)); // 언팔 대상

        // followingList 비워서 팔로우 상태 아님을 시뮬레이션
        when(user1.getFollowingList()).thenReturn(new ArrayList<>());

        // when & then
        BaseException exception = assertThrows(BaseException.class, () ->
                userService.unfollow(2L, 1L)
        );

        assertThat(exception.getStatus()).isEqualTo(ErrorCode.NOT_FOLLOWING.getStatus());
        assertThat(exception.getMessage()).isEqualTo(ErrorCode.NOT_FOLLOWING.getMessage());
    }


    @Test
    @DisplayName("팔로잉 목록 조회 성공")
    void getFollowings_success() {
        PageRequest pageable = PageRequest.of(0, 10);
        Page<FollowingResponse> page = new PageImpl<>(List.of());

        when(userReader.findFollowingsByUserId(1L, pageable)).thenReturn(page);

        Page<FollowingResponse> result = userService.getFollowings(1L, pageable);

        assertThat(result).isNotNull();
        verify(userReader).findFollowingsByUserId(1L, pageable);
    }
}
