package org.example.pdnight.domain.auth.application.authUseCase;

import at.favre.lib.crypto.bcrypt.BCrypt;
import org.example.pdnight.domain.auth.domain.AuthCommander;
import org.example.pdnight.domain.auth.domain.AuthProducer;
import org.example.pdnight.domain.auth.domain.entity.Auth;
import org.example.pdnight.domain.auth.presentation.dto.request.LoginRequest;
import org.example.pdnight.domain.auth.presentation.dto.request.SignupRequest;
import org.example.pdnight.domain.auth.presentation.dto.request.UserPasswordUpdateRequest;
import org.example.pdnight.domain.auth.presentation.dto.request.WithdrawRequest;
import org.example.pdnight.global.common.enums.ErrorCode;
import org.example.pdnight.global.common.enums.UserRole;
import org.example.pdnight.global.common.exception.BaseException;
import org.example.pdnight.global.config.PasswordEncoder;
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
public class AuthCommanderServiceTest {

    @InjectMocks
    private AuthCommanderService authService;

    @Mock
    private AuthCommander authCommander;

    @Mock
    private AuthProducer producer;

    @Mock
    private PasswordEncoder passwordEncoder;


    @Test
    @DisplayName("중복 이메일로 회원가입 테스트")
    void signUpDuplicatedEmail() {
        // given
        Long authId = 1L;
        Auth auth = mock(Auth.class);
        lenient().when(auth.getId()).thenReturn(authId);

        when(authCommander.findByEmail(any())).thenReturn(Optional.of(auth));

        //when
        BaseException exception = assertThrows(BaseException.class, () ->
                authService.signup(mock(SignupRequest.class))
        );

        //then
        assertEquals(ErrorCode.EMAIL_ALREADY_EXISTS.getStatus(), exception.getStatus());
        assertEquals(ErrorCode.EMAIL_ALREADY_EXISTS.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("탈퇴한 회원정보로 로그인 테스트")
    void loginWithdrawUser() {
        // given
        String email = "test@test.com";
        LoginRequest request = mock(LoginRequest.class);
        when(request.getEmail()).thenReturn(email);

        // Auth auth = getAuthByEmail(request.getEmail());
        Auth auth = mock(Auth.class);
        when(authCommander.findByEmail(request.getEmail())).thenReturn(Optional.of(auth));

        // validateAuth(auth, request.getPassword());
        when(auth.getIsDeleted()).thenReturn(true);

        //when
        BaseException exception = assertThrows(BaseException.class, () ->
                authService.login(request)
        );

        //then
        assertEquals(ErrorCode.AUTH_DEACTIVATED.getStatus(), exception.getStatus());
        assertEquals(ErrorCode.AUTH_DEACTIVATED.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("틀린 비밀번호로 로그인 테스트")
    void loginWrongPassword() {
        //given
        // Auth auth = getAuthByEmail(request.getEmail());
        Auth auth = mock(Auth.class);
        when(authCommander.findByEmail(any())).thenReturn(Optional.of(auth));

        // validateAuth(auth, request.getPassword());
        when(auth.getIsDeleted()).thenReturn(false);
        when(passwordEncoder.matches(any(), any())).thenReturn(false);

        //when
        BaseException exception = assertThrows(BaseException.class, () ->
                authService.login(mock(LoginRequest.class))
        );

        //then
        assertEquals(ErrorCode.INVALID_PASSWORD.getStatus(), exception.getStatus());
        assertEquals(ErrorCode.INVALID_PASSWORD.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("회원 탈퇴 기능 동작 테스트")
    void withdraw() {
        //given
        Long userId = 1L;
        // Auth auth = getAuthById(userId);
        Auth auth = mock(Auth.class);
        when(authCommander.findById(userId)).thenReturn(Optional.of(auth));

        // validateAuth(auth, request.getPassword());
        when(auth.getIsDeleted()).thenReturn(false);
        when(passwordEncoder.matches(any(), any())).thenReturn(true);
        doNothing().when(producer).produce(anyString(), any());

        //when
        authService.withdraw(userId, mock(WithdrawRequest.class));

        //then
        verify(auth).softDelete();
    }

    @Test
    @DisplayName("비밀번호 변경 기능 동작 테스트")
    void updatePassword() {
        //given
        Long userId = 1L;
        String email = "test@test.com";
        String oldPassword = "oldPassword";
        String newPassword = "newPassword";
        String encodedOldPW = BCrypt.withDefaults().hashToString(10, oldPassword.toCharArray());
        Auth auth = Auth.create(email, encodedOldPW, UserRole.USER);

        UserPasswordUpdateRequest request = mock(UserPasswordUpdateRequest.class);
        when(request.getOldPassword()).thenReturn(oldPassword);
        when(request.getNewPassword()).thenReturn(newPassword);

        // Auth auth = getAuthById(userId);
        when(authCommander.findById(userId)).thenReturn(Optional.of(auth));

        //when
        authService.updatePassword(userId, request);

        //then
        verify(authCommander).save(auth);
        assertTrue(BCrypt.verifyer().verify(newPassword.toCharArray(), auth.getPassword()).verified);
    }
}
