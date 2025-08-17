package org.example.pdnight.domain.auth.application.authUseCase;

import org.example.pdnight.domain.auth.presentation.dto.response.LoginResponse;

public interface OAuthService {

    String getAuthorizationUrl();

    LoginResponse loginWithOAuth(String code, String state);

}
