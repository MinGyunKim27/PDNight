package org.example.pdnight.domain.auth.application.authUseCase;

import org.example.pdnight.domain.auth.presentation.dto.request.LoginRequestDto;
import org.example.pdnight.domain.auth.presentation.dto.request.SignupRequestDto;
import org.example.pdnight.domain.auth.presentation.dto.request.UserPasswordUpdateRequest;
import org.example.pdnight.domain.auth.presentation.dto.request.WithdrawRequestDto;
import org.example.pdnight.domain.auth.presentation.dto.response.LoginResponseDto;
import org.example.pdnight.domain.auth.presentation.dto.response.SignupResponseDto;

public interface AuthService {

    SignupResponseDto signup(SignupRequestDto request);

    LoginResponseDto login(LoginRequestDto request);

    void updatePassword(Long userId, UserPasswordUpdateRequest requestDto);

    void logout(String token);

    void withdraw(Long userId, WithdrawRequestDto request);
}
