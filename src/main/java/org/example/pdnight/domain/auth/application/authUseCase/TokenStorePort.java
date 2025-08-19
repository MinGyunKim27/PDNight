package org.example.pdnight.domain.auth.application.authUseCase;

public interface TokenStorePort {

    void saveRefreshToken(Long userId, String refreshToken, long expirationMillis);

    String getRefreshToken(Long userId);

    void deleteRefreshToken(Long userId);

    void blacklistAccessToken(String token);
}
