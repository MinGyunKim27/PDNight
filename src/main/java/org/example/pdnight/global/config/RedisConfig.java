package org.example.pdnight.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@EnableCaching
@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Object> popularSearch(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> popularSearch = new RedisTemplate<>();
        popularSearch.setConnectionFactory(connectionFactory);

        // LocalDateTime 직렬화 사전 작업
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

        popularSearch.setKeySerializer(new StringRedisSerializer());
        popularSearch.setValueSerializer(new GenericJackson2JsonRedisSerializer(objectMapper));

        return popularSearch;
    }
}
