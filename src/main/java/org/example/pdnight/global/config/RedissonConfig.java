package org.example.pdnight.global.config;

import org.redisson.Redisson;
import org.redisson.api.RBucket;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableAspectJAutoProxy
public class RedissonConfig {
    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port}")
    private int redisPort;

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://" + redisHost + ":" + redisPort)
                .setDatabase(0)
                .setConnectionPoolSize(64)  // 연결 풀 크기 명시적 설정
                .setConnectionMinimumIdleSize(10);

        RedissonClient client = Redisson.create(config);

        // 연결 테스트 강화
        try {
            RLock testLock = client.getLock("connection-test-lock");
            boolean acquired = testLock.tryLock(1, 5, TimeUnit.SECONDS);
            if (acquired) {
                System.out.println("🚨🚨🚨 Redis 락 테스트 성공");
                testLock.unlock();
            } else {
                System.out.println("🚨🚨🚨 Redis 락 테스트 실패");
            }
        } catch (Exception e) {
            System.out.println("🚨🚨🚨 Redis 락 테스트 예외: " + e.getMessage());
            e.printStackTrace();
        }

        return client;
    }
}