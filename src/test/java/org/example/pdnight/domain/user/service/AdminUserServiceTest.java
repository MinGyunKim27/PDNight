package org.example.pdnight.domain.user.service;

import org.example.pdnight.domain.common.enums.ErrorCode;
import org.example.pdnight.domain.common.exception.BaseException;
import org.example.pdnight.domain.user.dto.request.UserNicknameUpdateDto;
import org.example.pdnight.domain.user.dto.response.UserResponseDto;
import org.example.pdnight.domain.user.entity.User;
import org.example.pdnight.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminUserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AdminUserService adminUserService;

    @Test
    @DisplayName("관리자가 유저 닉네임 변경 성공")
    void 관리자가_유저_닉네임_변경_성공() {
        // given
        Long userId = 1L;
        User mockUser = Mockito.mock(User.class);
        UserNicknameUpdateDto dto = mock(UserNicknameUpdateDto.class);
        when(dto.getNickname()).thenReturn("수정된 닉네임");

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

        // when
        UserResponseDto response = adminUserService.updateNickname(userId, dto);

        // then
        assertNotNull(response);
        verify(mockUser).updateNickname("수정된 닉네임");
    }

    @Test
    @DisplayName("닉네임 변경 - 유저 없음 예외")
    void 닉네임_변경_유저없음_예외() {
        // given
        Long userId = 1L;
        UserNicknameUpdateDto dto = mock(UserNicknameUpdateDto.class);

        // when & then
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        BaseException exception = assertThrows(BaseException.class,
                () -> adminUserService.updateNickname(userId, dto));

        assertEquals(ErrorCode.USER_NOT_FOUND.getStatus(), exception.getStatus());
        assertEquals(ErrorCode.USER_NOT_FOUND.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("관리자가 회원 삭제 성공")
    void 관리자가_회원_삭제_성공() {
        // given
        Long userId = 1L;
        User mockUser = Mockito.mock(User.class);

        // when
        when(mockUser.getIsDeleted()).thenReturn(false);
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

        adminUserService.deleteUser(userId);

        // then
        verify(mockUser).softDelete();
    }

    @Test
    @DisplayName("관리자가 회원 삭제 - 이미 탈퇴된 유저 예외")
    void 회원삭제예외_이미_탈퇴된_유저() {
        // given
        Long userId = 1L;
        User mockUser = Mockito.mock(User.class);

        // when & then
        when(mockUser.getIsDeleted()).thenReturn(true);
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        BaseException exception = assertThrows(BaseException.class,
                () -> adminUserService.deleteUser(userId));

        assertEquals(ErrorCode.USER_DEACTIVATED.getStatus(), exception.getStatus());
        assertEquals(ErrorCode.USER_DEACTIVATED.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("회원 삭제 - 유저 없음 예외")
    void 회원삭제예외_유저없음() {
        // given
        Long userId = 1L;

        // when & then
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        BaseException exception = assertThrows(BaseException.class,
                () -> adminUserService.deleteUser(userId));

        assertEquals(ErrorCode.USER_NOT_FOUND.getStatus(), exception.getStatus());
        assertEquals(ErrorCode.USER_NOT_FOUND.getMessage(), exception.getMessage());
    }
}
