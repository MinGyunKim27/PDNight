package org.example.pdnight.domain.auth.dto.response;

import lombok.Getter;

@Getter
public class LoginResponseDto {
    private String token;

    private LoginResponseDto(String token) {
        this.token = token;
    }

    public static LoginResponseDto from(String token) {
        return new LoginResponseDto(token);
    }
}
