package org.example.pdnight.domain1.auth.service;

import org.example.pdnight.domain1.auth.dto.request.LoginRequestDto;
import org.example.pdnight.domain1.auth.dto.request.SignupRequestDto;
import org.example.pdnight.domain1.auth.dto.request.WithdrawRequestDto;
import org.example.pdnight.domain1.common.exception.BaseException;
import org.example.pdnight.domain1.user.entity.User;
import org.example.pdnight.domain1.user.repository.UserRepository;
import org.example.pdnight.global.config.PasswordEncoder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private User user;

    @Test
    @DisplayName("중복 이메일로 회원가입 테스트")
    void signUpDuplicatedEmail() {
        //given
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));

        //when
        BaseException result = assertThrows(BaseException.class, () -> authService.signup(mock(SignupRequestDto.class)));

        //then
        assertEquals(HttpStatus.CONFLICT,result.getStatus());
        assertEquals("이미 존재하는 이메일입니다",result.getMessage());
    }

    @Test
    @DisplayName("탈퇴한 회원정보로 로그인 테스트")
    void loginWithdrawUser() {
        //given
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(user.getIsDeleted()).thenReturn(true);
        //when
        BaseException result = assertThrows(BaseException.class, () -> authService.login(mock(LoginRequestDto.class)));

        //then
        assertEquals(HttpStatus.UNAUTHORIZED,result.getStatus());
        assertEquals("탈퇴 된 회원입니다.",result.getMessage());
    }

    @Test
    @DisplayName("틀린 비밀번호로 로그인 테스트")
    void loginWrongPassword() {
        //given
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(user.getIsDeleted()).thenReturn(false);
        when(passwordEncoder.matches(any(), any())).thenReturn(false);

        //when
        BaseException result = assertThrows(BaseException.class, () -> authService.login(mock(LoginRequestDto.class)));

        //then
        assertEquals(HttpStatus.BAD_REQUEST,result.getStatus());
        assertEquals("비밀번호가 일치하지 않습니다",result.getMessage());
    }

    @Test
    @DisplayName("회원 탈퇴 기능 동작 테스트")
    void withdraw() {
        //given
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(user.getIsDeleted()).thenReturn(false);
        when(passwordEncoder.matches(any(), any())).thenReturn(true);

        //when
        authService.withdraw(1L, mock(WithdrawRequestDto.class));

        //then
        verify(user).softDelete();
    }
}