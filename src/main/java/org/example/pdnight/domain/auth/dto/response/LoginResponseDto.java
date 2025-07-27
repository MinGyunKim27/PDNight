package org.example.pdnight.domain.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponseDto {
    private String token;

    public static LoginResponseDto from(String token) {
        return new LoginResponseDto(token);
    }
}
