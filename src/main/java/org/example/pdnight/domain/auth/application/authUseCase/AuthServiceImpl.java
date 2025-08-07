package org.example.pdnight.domain.auth.application.authUseCase;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.auth.presentation.dto.request.LoginRequest;
import org.example.pdnight.domain.auth.presentation.dto.request.SignupRequest;
import org.example.pdnight.domain.auth.presentation.dto.request.UserPasswordUpdateRequest;
import org.example.pdnight.domain.auth.presentation.dto.request.WithdrawRequest;
import org.example.pdnight.domain.auth.presentation.dto.response.LoginResponse;
import org.example.pdnight.domain.auth.presentation.dto.response.SignupResponse;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthCommanderService authCommanderService;

    @Override
    public SignupResponse signup(SignupRequest request) {
        return authCommanderService.signup(request);
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        return authCommanderService.login(request);
    }

    @Override
    public void updatePassword(Long userId, UserPasswordUpdateRequest requestDto) {
        authCommanderService.updatePassword(userId, requestDto);
    }

    @Override
    public void logout(HttpServletRequest http) {
        authCommanderService.logout(http);
    }

    @Override
    public void withdraw(Long userId, WithdrawRequest request) {
        authCommanderService.withdraw(userId, request);
    }
}
