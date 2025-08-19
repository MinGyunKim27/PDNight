package org.example.pdnight.domain.auth.presentation.dto.response;

import lombok.Getter;

@Getter
public class OAuthLoginResponse {
    private final String token;
    private final String refreshToken;
    private final boolean profileCompleted;

    private OAuthLoginResponse(String token, String refreshToken, boolean profileCompleted) {
        this.token = token;
        this.refreshToken = refreshToken;
        this.profileCompleted = profileCompleted;
    }

    public static OAuthLoginResponse from(String token, String refreshToken, boolean profileCompleted) {
        return new OAuthLoginResponse(token, refreshToken, profileCompleted);
    }
}
