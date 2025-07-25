package org.example.pdnight.domain.follow.service;

import org.example.pdnight.domain.common.exception.BaseException;
import org.example.pdnight.domain.common.helper.GetHelper;
import org.example.pdnight.domain.follow.dto.response.FollowResponseDto;
import org.example.pdnight.domain.follow.dto.response.FollowingResponseDto;
import org.example.pdnight.domain.follow.entity.Follow;
import org.example.pdnight.domain.follow.repository.FollowRepository;
import org.example.pdnight.domain.follow.repository.FollowRepositoryQuery;
import org.example.pdnight.domain.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FollowServiceTest {
    private FollowRepository followRepository;
    private FollowRepositoryQuery followRepositoryQuery;
    private GetHelper helper;
    private FollowService followService;

    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        followRepository = mock(FollowRepository.class);
        followRepositoryQuery = mock(FollowRepositoryQuery.class);
        helper = mock(GetHelper.class);

        followService = new FollowService(followRepository, followRepositoryQuery, helper);
        user1 = Mockito.mock(User.class);
        lenient().when(user1.getId()).thenReturn(1L);
        user2 = Mockito.mock(User.class);
        lenient().when(user2.getId()).thenReturn(1L);
    }

    @Test
    @DisplayName("성공적으로 팔로우를 수행한다")
    void follow_success() {
        when(helper.getUserById(1L)).thenReturn(user1);
        when(helper.getUserById(2L)).thenReturn(user2);
        when(followRepository.existsByFollowerAndFollowing(user2, user1)).thenReturn(false);

        FollowResponseDto result = followService.follow(1L, 2L);

        assertThat(result).isNotNull();
        verify(followRepository, times(1)).save(any(Follow.class));
    }

    @Test
    @DisplayName("자기 자신을 팔로우하면 예외가 발생한다")
    void follow_self_fail() {
        BaseException exception = assertThrows(BaseException.class, () ->
                followService.follow(1L, 1L)
        );
        assertThat(exception.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("이미 팔로우한 사용자에게 팔로우 시도 시 예외 발생")
    void follow_duplicate_fail() {
        when(helper.getUserById(1L)).thenReturn(user1);
        when(helper.getUserById(2L)).thenReturn(user2);
        when(followRepository.existsByFollowerAndFollowing(user2, user1)).thenReturn(true);

        BaseException exception = assertThrows(BaseException.class, () ->
                followService.follow(1L, 2L)
        );
        assertThat(exception.getStatus()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    @DisplayName("언팔로우 성공")
    void unfollow_success() {
        when(helper.getUserById(1L)).thenReturn(user1);
        when(helper.getUserById(2L)).thenReturn(user2);
        when(followRepository.findByFollowerAndFollowing(user2, user1)).thenReturn(Optional.of(Follow.create(user2, user1)));

        followService.unfollow(1L, 2L);

        verify(followRepository).delete(any(Follow.class));
    }

    @Test
    @DisplayName("언팔로우: 자기 자신을 언팔 시도할 경우 예외 발생")
    void unfollow_self_fail() {
        BaseException exception = assertThrows(BaseException.class, () ->
                followService.unfollow(1L, 1L)
        );
        assertThat(exception.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("언팔로우: 팔로우 중이 아니면 예외 발생")
    void unfollow_not_following_fail() {
        when(helper.getUserById(1L)).thenReturn(user1);
        when(helper.getUserById(2L)).thenReturn(user2);
        when(followRepository.findByFollowerAndFollowing(user2, user1)).thenReturn(Optional.empty());

        BaseException exception = assertThrows(BaseException.class, () ->
                followService.unfollow(1L, 2L)
        );
        assertThat(exception.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("팔로잉 목록 조회 성공")
    void getFollowings_success() {
        PageRequest pageable = PageRequest.of(0, 10);
        Page<FollowingResponseDto> page = new PageImpl<>(List.of());
        when(followRepositoryQuery.findFollowingsByUserId(1L, pageable)).thenReturn(page);

        Page<FollowingResponseDto> result = followService.getFollowings(1L, pageable);

        assertThat(result).isNotNull();
        verify(followRepositoryQuery).findFollowingsByUserId(1L, pageable);
    }
}
