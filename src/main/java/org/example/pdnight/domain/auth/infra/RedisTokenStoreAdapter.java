package org.example.pdnight.domain.auth.infra;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.auth.application.authUseCase.TokenStorePort;
import org.example.pdnight.global.constant.CacheName;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedisTokenStoreAdapter implements TokenStorePort {

    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public void saveRefreshToken(Long userId, String refreshToken, long expirationMillis) {
        redisTemplate.opsForValue().set(
                "RT:" + userId,
                refreshToken,
                expirationMillis,
                TimeUnit.MILLISECONDS
        );
    }

    @Override
    public String getRefreshToken(Long userId) {
        return redisTemplate.opsForValue().get("RT:" + userId);
    }

    @Override
    public void deleteRefreshToken(Long userId) {
        redisTemplate.delete("RT:" + userId);
    }

    @Override
    public void blacklistAccessToken(String token) {
        redisTemplate.opsForSet().add(CacheName.BLACKLIST_TOKEN, token);
    }
}
