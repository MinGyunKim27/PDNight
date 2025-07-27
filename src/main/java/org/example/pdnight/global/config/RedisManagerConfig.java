package org.example.pdnight.global.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.pdnight.domain.common.dto.PagedResponse;
import org.example.pdnight.global.constant.CacheName;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class RedisManagerConfig {

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        // <String, PostResponseWithApplyStatusDto>
        RedisCacheConfiguration cachePageProductConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(getKeySerializer())  // key 직렬화
                .serializeValuesWith(getValuePageSerializer())  // value 직렬화
                .entryTtl(Duration.ofMinutes(10)); // 캐시 시간 설정

        // 여러 형태의 직렬화 설정 적용
        Map<String, RedisCacheConfiguration> cacheConfigs = new HashMap<>();
        cacheConfigs.put(CacheName.SEARCH_POST, cachePageProductConfiguration);

        return RedisCacheManager.builder(redisConnectionFactory)
                .withInitialCacheConfigurations(cacheConfigs)
                .build();
    }

    // key 직렬화 : String
    private RedisSerializationContext.SerializationPair<String> getKeySerializer() {
        return RedisSerializationContext
                .SerializationPair
                .fromSerializer(new StringRedisSerializer());
    }

    // value 직렬화 : PostResponseWithApplyStatusDto
    private RedisSerializationContext.SerializationPair<PagedResponse> getValuePageSerializer() {
        // LocalDateTime 직렬화 사전 작업
        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        return RedisSerializationContext
                .SerializationPair
                .fromSerializer(new Jackson2JsonRedisSerializer<>(objectMapper, PagedResponse.class));
    }
}
