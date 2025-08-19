package org.example.pdnight.domain.auth.presentation.dto.response;

import lombok.Getter;

@Getter
public class LoginResponse {
    private final String token;
    private final String refreshToken;

    private LoginResponse(String token, String refreshToken) {
        this.token = token;
        this.refreshToken = refreshToken;
    }

    public static LoginResponse from(String token, String refreshToken) {
        return new LoginResponse(token, refreshToken);
    }
}
