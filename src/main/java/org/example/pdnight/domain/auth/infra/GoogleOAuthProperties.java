package org.example.pdnight.domain.auth.infra;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "oauth2.google")
@Getter
@Setter
public class GoogleOAuthProperties {
    private String clientId;
    private String clientSecret;
    private String redirectUri;
    private String authorizationEndpoint;
    private String tokenEndpoint;
    private String userInfoEndpoint;
}
