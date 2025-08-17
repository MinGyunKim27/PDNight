package org.example.pdnight.domain.auth.application.authUseCase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.pdnight.domain.auth.domain.AuthCommander;
import org.example.pdnight.domain.auth.domain.AuthProducer;
import org.example.pdnight.domain.auth.domain.entity.Auth;
import org.example.pdnight.domain.auth.presentation.dto.response.LoginResponse;
import org.example.pdnight.global.common.enums.ErrorCode;
import org.example.pdnight.global.common.enums.KafkaTopic;
import org.example.pdnight.global.common.enums.UserRole;
import org.example.pdnight.global.common.exception.BaseException;
import org.example.pdnight.global.event.AuthSignedUpEvent;
import org.example.pdnight.global.utils.JwtUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
@RequiredArgsConstructor
public class OAuthServiceImpl implements OAuthService {

    private final OAuthProviderInfo providerInfo;
    private final HttpClientPort httpClient;
    private final OAuthUrlBuilder urlBuilder;
    private final AuthCommander authCommander;
    private final JwtUtil jwtUtil;
    //private final UserQueryPort userQueryPort;
    private final AuthProducer producer;

    private final SecureRandom secureRandom = new SecureRandom();
    private final Map<String, String> codeVerifierStore = new ConcurrentHashMap<>();

    /**
     * OAuth 로그인 URL 생성
     * - PKCE(Proof Key for Code Exchange) 보안 적용
     * - code_challenge 생성 및 state 저장
     */
    @Override
    public String getAuthorizationUrl() {

        String codeVerifier;
        String codeChallenge;

        try {
            // code_verifier 생성
            byte[] code = new byte[32];
            // 랜덤 문자열 생성후 base64로 인코딩
            secureRandom.nextBytes(code);
            codeVerifier = Base64.getUrlEncoder().withoutPadding().encodeToString(code);

            // code_challenge 생성
            // 사용할 해시 알고리즘 SHA-256
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            // code_verifier 해시처리
            byte[] digest = md.digest(codeVerifier.getBytes(StandardCharsets.US_ASCII));
            // base64로 인코딩
            codeChallenge = Base64.getUrlEncoder().withoutPadding().encodeToString(digest);
        } catch (Exception e) {
            log.error("PKCE  에러 발생 {}", e.getMessage());
            throw new BaseException(ErrorCode.OAUTH_PKCE_GENERATION_ERROR);
        }

        // CSRF(Cross-Site Request Forgery) 방지 상태값 생성 후 codeVerifier 저장
        String state = UUID.randomUUID().toString();
        codeVerifierStore.put(state, codeVerifier);

        // 인가 URL 생성
        return urlBuilder.buildUrl(
                providerInfo.getClientId(),
                providerInfo.getRedirectUri(),
                state,
                codeChallenge
        );
    }

    /**
     * OAuth 콜백 이후 처리
     * - 인가 코드(code)를 통해 토큰 요청
     * - access_token으로 사용자 정보 조회
     */
    @Override
    public LoginResponse loginWithOAuth(String code, String state) {
        String codeVerifier = codeVerifierStore.remove(state);

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "authorization_code");
        form.add("code", code);
        form.add("redirect_uri", providerInfo.getRedirectUri());
        form.add("client_id", providerInfo.getClientId());
        form.add("client_secret", providerInfo.getClientSecret());
        form.add("code_verifier", codeVerifier);

        Map<String, Object> tokenResponse = httpClient.postForm(providerInfo.getTokenEndpoint(), form);
        String accessToken = (String) tokenResponse.get("access_token");

        Map<String, Object> userInfo = httpClient.get(providerInfo.getUserInfoEndpoint(), accessToken);
        String email = (String) userInfo.get("email");
        String name = (String) userInfo.get("name");

        // 이메일로 가입된 계정이 있다면 로그인 없으면 회원가입 진행
        Auth auth = authCommander.findByEmail(email).orElseGet(() -> {
            Auth newAuth = Auth.create(email, null, UserRole.USER);
            Auth saved = authCommander.save(newAuth);

            producer.produce(KafkaTopic.AUTH_SIGNED_UP.topicName(), new AuthSignedUpEvent(
                    saved.getId(),
                    name,
                    "social_user_" + UUID.randomUUID().toString().substring(0, 8),
                    null,
                    null,
                    null
            ));

            return saved;
        });

        // Todo: 로그인완료시 API 추후 호출해서 JWT에 추가하는 방식으로 진행해야할꺼같음
        //UserInfo user = userQueryPort.getUserInfoById(auth.getId());

        String token = jwtUtil.createToken(
                auth.getId(), auth.getRole(), null,
                null, null, null);
        return LoginResponse.from(token);
    }

}

