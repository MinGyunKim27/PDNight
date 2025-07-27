package org.example.pdnight.global.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.pdnight.domain.common.dto.PagedResponse;
import org.example.pdnight.domain.post.dto.response.PostResponseWithApplyStatusDto;
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

    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        // 여러 형태의 직렬화 설정 적용
        Map<String, RedisCacheConfiguration> cacheConfigs = new HashMap<>();
        cacheConfigs.put(CacheName.SEARCH_POST, configPagedResponse());
        cacheConfigs.put(CacheName.LIKED_POST, configPagedResponse());
        cacheConfigs.put(CacheName.CONFIRMED_POST, configPagedResponse());
        cacheConfigs.put(CacheName.WRITTEN_POST, configPagedResponse());
        cacheConfigs.put(CacheName.SUGGESTED_POST, configPagedResponse());
        cacheConfigs.put(CacheName.ONE_POST, configPostResponseWithApplyStatusDto());

        return RedisCacheManager.builder(redisConnectionFactory)
                .withInitialCacheConfigurations(cacheConfigs)
                .build();
    }

    // PagedResponse 직렬화 설정
    private RedisCacheConfiguration configPagedResponse() {
        return RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(getKeySerializer()) // key 직렬화
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new Jackson2JsonRedisSerializer<>(
                                OBJECT_MAPPER, PagedResponse.class)
                        )) // value 직렬화
                .entryTtl(Duration.ofMinutes(10)); // 캐시 시간
    }

    // PostResponseWithApplyStatusDto 직렬화 설정
    private RedisCacheConfiguration configPostResponseWithApplyStatusDto() {
        return RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(getKeySerializer()) // key 직렬화
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new Jackson2JsonRedisSerializer<>(
                                OBJECT_MAPPER, PostResponseWithApplyStatusDto.class)
                        )) // value 직렬화
                .entryTtl(Duration.ofMinutes(10)); // 캐시 시간
    }

    // key 직렬화 : String
    private RedisSerializationContext.SerializationPair<String> getKeySerializer() {
        return RedisSerializationContext
                .SerializationPair
                .fromSerializer(new StringRedisSerializer());
    }
}
