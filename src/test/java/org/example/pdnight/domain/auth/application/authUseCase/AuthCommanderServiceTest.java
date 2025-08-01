package org.example.pdnight.domain.auth.application.authUseCase;

import org.example.pdnight.domain.auth.domain.AuthReader;
import org.example.pdnight.domain.auth.domain.entity.Auth;
import org.example.pdnight.domain.auth.presentation.dto.request.SignupRequest;
import org.example.pdnight.global.common.exception.BaseException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthCommanderServiceTest {

    @InjectMocks
    private AuthCommanderService authCommanderService;
    @Mock
    private AuthReader authReader;

    @Test
    @DisplayName("중복 이메일로 회원가입 테스트")
    void signUpDuplicatedEmail() {
        // given
        Long authId = 1L;
        Auth mockAuth = mock(Auth.class);
        lenient().when(mockAuth.getId()).thenReturn(authId);

        when(authReader.findByEmail(any())).thenReturn(Optional.of(mockAuth));

        //when
        BaseException exception = assertThrows(BaseException.class, () ->
                authCommanderService.signup(mock(SignupRequest.class))
        );
        //then
        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
        assertEquals("이미 존재하는 이메일입니다", exception.getMessage());
    }
}
