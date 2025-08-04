package org.example.pdnight.domain.auth.presentation.dto.response;

import lombok.Getter;

@Getter
public class LoginResponse {
    private String token;

    private LoginResponse(String token) {
        this.token = token;
    }

    public static LoginResponse from(String token) {
        return new LoginResponse(token);
    }
}
