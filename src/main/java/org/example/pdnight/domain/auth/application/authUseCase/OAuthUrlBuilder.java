package org.example.pdnight.domain.auth.application.authUseCase;

public interface OAuthUrlBuilder {
    String buildUrl(String clientId, String redirectUri, String state, String codeChallenge);
}
