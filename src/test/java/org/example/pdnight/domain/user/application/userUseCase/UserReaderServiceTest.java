package org.example.pdnight.domain.user.application.userUseCase;

import org.example.pdnight.domain.user.domain.entity.User;
import org.example.pdnight.domain.user.domain.entity.UserCoupon;
import org.example.pdnight.domain.user.domain.userDomain.UserReader;
import org.example.pdnight.domain.user.presentation.dto.userDto.response.FollowingResponse;
import org.example.pdnight.domain.user.presentation.dto.userDto.response.UserCouponResponse;
import org.example.pdnight.domain.user.presentation.dto.userDto.response.UserEvaluationResponse;
import org.example.pdnight.domain.user.presentation.dto.userDto.response.UserResponse;
import org.example.pdnight.global.common.dto.PagedResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserReaderServiceTest {
    @Mock
    private UserReader userReader;
    @Mock
    private UserInfoAssembler userInfoAssembler;
    @InjectMocks
    private UserReaderService userReaderService;

    /**
     *
     */
    @Test
    void 내_프로필_조회_성공() {
        // given
        Long userId = 1L;
        User user = mock(User.class); // 혹은 new User(...)로 진짜 객체 생성
        UserResponse response = mock(UserResponse.class);

        when(userReader.findById(userId)).thenReturn(Optional.of(user));
        when(userInfoAssembler.toDto(user)).thenReturn(response);
        when(response.getId()).thenReturn(userId);
        when(response.getName()).thenReturn("Test");

        // when
        UserResponse result = userReaderService.getProfile(userId);

        // then
        assertEquals(userId, result.getId());
        assertEquals("Test", result.getName());
    }

    @Test
    void 유저_평가_조회_성공() {
        // given
        Long userId = 1L;
        User user = mock(User.class);

        when(userReader.findById(userId)).thenReturn(Optional.of(user));
        when(user.getId()).thenReturn(userId);
        when(user.getTotalRate()).thenReturn(10L);
        when(user.getTotalReviewer()).thenReturn(2L);

        // when
        UserEvaluationResponse result = userReaderService.getEvaluation(userId);

        // then
        assertEquals(userId, result.getId());
        assertEquals(5f, result.getRate());
    }

    @Test
    void 팔로워_조회_성공() {
        Long userId = 1L;
        String nickname = "James";
        Pageable pageable = PageRequest.of(0, 10);
        List<FollowingResponse> followingResponses = List.of(
                FollowingResponse.create(userId, nickname)
        );
        PageImpl<FollowingResponse> pagingResponses =
                new PageImpl<>(followingResponses, pageable, followingResponses.size());

        when(userReader.findFollowingsByUserId(userId,pageable)).thenReturn(pagingResponses);

        PagedResponse<FollowingResponse> response = userReaderService.getFollowings(1L,pageable);

        assertThat(response.contents()).hasSize(1);
        assertThat(response.contents().get(0).getNickname()).isEqualTo("James");

    }

    @Test
    void 유효한_쿠폰이_존재하는_경우() {
        // given
        Long userId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        UserCoupon userCoupon = UserCoupon.create(mock(User.class),1L,5);

        List<UserCouponResponse> couponList = List.of(
                new UserCouponResponse(userCoupon)
        );
        Page<UserCouponResponse> couponPage = new PageImpl<>(couponList, pageable, couponList.size());

        when(userReader.findUserCoupons(eq(userId), any(LocalDateTime.class), eq(pageable)))
                .thenReturn(couponPage);

        // when
        PagedResponse<UserCouponResponse> result = userReaderService.getValidCoupons(userId, pageable);

        // then
        assertThat(result.contents()).hasSize(1);
        assertThat(result.contents().get(0).getCouponId()).isEqualTo(1L);
    }

    @Test
    void 유효한_쿠폰이_없는_경우() {
        // given
        Long userId = 2L;
        Pageable pageable = PageRequest.of(0, 10);
        Page<UserCouponResponse> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(userReader.findUserCoupons(eq(userId), any(LocalDateTime.class), eq(pageable)))
                .thenReturn(emptyPage);

        // when
        PagedResponse<UserCouponResponse> result = userReaderService.getValidCoupons(userId, pageable);

        // then
        assertThat(result.contents()).isEmpty();
    }

    @Test
    void 만료된_쿠폰만_존재하는_경우() {
        // given
        Long userId = 3L;
        Pageable pageable = PageRequest.of(0, 10);
        // 만료된 쿠폰은 Repository에서 이미 필터링된다고 가정하고 빈 결과를 반환
        Page<UserCouponResponse> expiredOnlyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(userReader.findUserCoupons(eq(userId), any(LocalDateTime.class), eq(pageable)))
                .thenReturn(expiredOnlyPage);

        // when
        PagedResponse<UserCouponResponse> result = userReaderService.getValidCoupons(userId, pageable);

        // then
        assertThat(result.contents()).isEmpty();
    }

    @Test
    void 이름이나_닉네임으로_검색결과가_존재하는_경우() {
        // given
        String keyword = "알";
        Pageable pageable = PageRequest.of(0, 10);

        User user = User.createTestUser(1L, "Alice", "alice@email.com", "1234");
        user.updateNickname("알리");

        UserResponse dto = UserResponse.from(user,new ArrayList<>(),new ArrayList<>());

        Page<User> userPage = new PageImpl<>(List.of(user), pageable, 1);
        List<UserResponse> dtoList = List.of(dto);

        when(userReader.searchUsers(eq(keyword), eq(pageable))).thenReturn(userPage);
        when(userInfoAssembler.toDtoList(userPage)).thenReturn(dtoList);

        // when
        PagedResponse<UserResponse> result = userReaderService.searchUsers(keyword, pageable);

        // then
        assertThat(result.contents()).hasSize(1);
        assertThat(result.contents().get(0).getNickname()).isEqualTo("알리");
    }

    @Test
    void 검색결과가_없는_경우() {
        // given
        String keyword = "존재하지않음";
        Pageable pageable = PageRequest.of(0, 10);

        Page<User> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(userReader.searchUsers(eq(keyword), eq(pageable))).thenReturn(emptyPage);
        when(userInfoAssembler.toDtoList(emptyPage)).thenReturn(Collections.emptyList());

        // when
        PagedResponse<UserResponse> result = userReaderService.searchUsers(keyword, pageable);

        // then
        assertThat(result.contents()).isEmpty();
        assertThat(result.totalElements()).isZero();
    }

    @Test
    void 탈퇴한_유저는_서비스_단에서_조회되지_않음() {
        // given
        String keyword = "탈퇴유저";
        Pageable pageable = PageRequest.of(0, 10);

        // Repository에서 이미 걸러졌다고 가정
        Page<User> page = new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(userReader.searchUsers(eq(keyword), eq(pageable))).thenReturn(page);
        when(userInfoAssembler.toDtoList(page)).thenReturn(Collections.emptyList());

        // when
        PagedResponse<UserResponse> result = userReaderService.searchUsers(keyword, pageable);

        // then
        assertThat(result.contents()).isEmpty();
    }

    @Test
    void 어드민_조회_성공() {
        // given
        Pageable pageable = PageRequest.of(0, 10);

        User user = User.createTestUser(1L, "Alice", "alice@email.com", "1234");

        UserResponse dto = UserResponse.from(user,new ArrayList<>(),new ArrayList<>());

        Page<User> userPage = new PageImpl<>(List.of(user), pageable, 1);
        List<UserResponse> dtoList = List.of(dto);

        when(userReader.findAll(pageable)).thenReturn(userPage);
        when(userInfoAssembler.toDtoList(userPage)).thenReturn(dtoList);

        // when
        PagedResponse<UserResponse> result = userReaderService.getAllUsers(pageable);

        // then
        assertThat(result.contents()).hasSize(1);
        assertThat(result.contents().get(0).getName()).isEqualTo("Alice");
    }
}
