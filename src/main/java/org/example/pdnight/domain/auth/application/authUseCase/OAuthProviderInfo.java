package org.example.pdnight.domain.auth.application.authUseCase;

public interface OAuthProviderInfo {

    String getClientId();

    String getClientSecret();

    String getRedirectUri();

    String getAuthorizationEndpoint();

    String getTokenEndpoint();

    String getUserInfoEndpoint();

}
