package org.example.pdnight.domain.auth.application.authUseCase;

import jakarta.servlet.http.HttpServletRequest;
import org.example.pdnight.domain.auth.presentation.dto.request.LoginRequest;
import org.example.pdnight.domain.auth.presentation.dto.request.SignupRequest;
import org.example.pdnight.domain.auth.presentation.dto.request.UserPasswordUpdateRequest;
import org.example.pdnight.domain.auth.presentation.dto.request.WithdrawRequest;
import org.example.pdnight.domain.auth.presentation.dto.response.LoginResponse;
import org.example.pdnight.domain.auth.presentation.dto.response.SignupResponse;

public interface AuthService {

    SignupResponse signup(SignupRequest request);

    LoginResponse login(LoginRequest request);

    void updatePassword(Long userId, UserPasswordUpdateRequest requestDto);

    void logout(HttpServletRequest http);

    void withdraw(Long userId, WithdrawRequest request);

    LoginResponse reissue(String refreshTokenHeader);
}
