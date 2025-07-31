package org.example.pdnight.domain.auth.application.authUseCase;

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

    private final AuthCommandService authCommandService;
    private final AuthQueryService authQueryService;

    @Override
    public SignupResponse signup(SignupRequest request) {
        return authCommandService.signup(request);
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        return authCommandService.login(request);
    }

    @Override
    public void updatePassword(Long userId, UserPasswordUpdateRequest requestDto) {
        authCommandService.updatePassword(userId, requestDto);
    }

    @Override
    public void logout(String token) {
        authCommandService.logout(token);
    }

    @Override
    public void withdraw(Long userId, WithdrawRequest request) {
        authCommandService.withdraw(userId, request);
    }
}
