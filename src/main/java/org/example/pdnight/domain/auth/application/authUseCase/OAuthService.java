package org.example.pdnight.domain.auth.application.authUseCase;

import org.example.pdnight.domain.auth.presentation.dto.response.OAuthLoginResponse;

public interface OAuthService {

    String getAuthorizationUrl();

    OAuthLoginResponse loginWithOAuth(String code, String state);

}
