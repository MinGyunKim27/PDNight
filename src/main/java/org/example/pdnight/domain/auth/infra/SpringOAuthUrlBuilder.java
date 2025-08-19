package org.example.pdnight.domain.auth.infra;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.auth.application.authUseCase.OAuthProviderInfo;
import org.example.pdnight.domain.auth.application.authUseCase.OAuthUrlBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@RequiredArgsConstructor
public class SpringOAuthUrlBuilder implements OAuthUrlBuilder {

    private final OAuthProviderInfo providerInfo;

    @Override
    public String buildUrl(String clientId, String redirectUri, String state, String codeChallenge) {
        return UriComponentsBuilder.fromUriString(providerInfo.getAuthorizationEndpoint())
                .queryParam("client_id", clientId)
                .queryParam("redirect_uri", redirectUri)
                .queryParam("response_type", "code")
                .queryParam("scope", "openid email profile")
                .queryParam("state", state)
                .queryParam("code_challenge", codeChallenge)
                .queryParam("code_challenge_method", "S256")
                .toUriString();
    }
}
