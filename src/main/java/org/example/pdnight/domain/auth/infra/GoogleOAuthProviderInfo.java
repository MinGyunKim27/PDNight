package org.example.pdnight.domain.auth.infra;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.auth.application.authUseCase.OAuthProviderInfo;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GoogleOAuthProviderInfo implements OAuthProviderInfo {

    private final GoogleOAuthProperties props;

    @Override public String getClientId() {
        return props.getClientId();
    }

    @Override public String getClientSecret() {
        return props.getClientSecret();
    }

    @Override public String getRedirectUri() {
        return props.getRedirectUri();
    }

    @Override public String getAuthorizationEndpoint() {
        return props.getAuthorizationEndpoint();
    }

    @Override public String getTokenEndpoint() {
        return props.getTokenEndpoint();
    }

    @Override public String getUserInfoEndpoint() {
        return props.getUserInfoEndpoint();
    }

}
