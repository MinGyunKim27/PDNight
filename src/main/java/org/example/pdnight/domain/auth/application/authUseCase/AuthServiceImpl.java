package org.example.pdnight.domain.auth.application.authUseCase;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.auth.presentation.dto.request.LoginRequestDto;
import org.example.pdnight.domain.auth.presentation.dto.request.SignupRequestDto;
import org.example.pdnight.domain.auth.presentation.dto.request.UserPasswordUpdateRequest;
import org.example.pdnight.domain.auth.presentation.dto.request.WithdrawRequestDto;
import org.example.pdnight.domain.auth.presentation.dto.response.LoginResponseDto;
import org.example.pdnight.domain.auth.presentation.dto.response.SignupResponseDto;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthCommandService authCommandService;
    private final AuthQueryService authQueryService;

    @Override
    public SignupResponseDto signup(SignupRequestDto request) {
        return authCommandService.signup(request);
    }

    @Override
    public LoginResponseDto login(LoginRequestDto request) {
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
    public void withdraw(Long userId, WithdrawRequestDto request) {
        authCommandService.withdraw(userId, request);
    }
}
