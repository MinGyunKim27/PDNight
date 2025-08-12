package org.example.pdnight.global.config.kafka;


import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.security.oauthbearer.internals.secured.ValidateException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.DeserializationException;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.messaging.converter.MessageConversionException;
import org.springframework.messaging.handler.invocation.MethodArgumentResolutionException;
import org.springframework.util.backoff.ExponentialBackOff;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class KafkaNotificationConsumerConfig {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    // 알림 전용 리스너(실패 시 .notification.DLT로 보냄)
    @Bean(name = "notificationListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, Object> notificationListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory = new ConcurrentKafkaListenerContainerFactory<>();
        kafkaListenerContainerFactory.setConsumerFactory(notificationConsumerFactory());
        kafkaListenerContainerFactory.setCommonErrorHandler(notificationErrorHandler());
        return kafkaListenerContainerFactory;
    }

    // 알림 DLT 처리 전용 리스너 (재실패 시 parking으로)
    @Bean(name = "notificationDltListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, Object> notificationDltListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory = new ConcurrentKafkaListenerContainerFactory<>();
        kafkaListenerContainerFactory.setConsumerFactory(notificationConsumerFactory());
        kafkaListenerContainerFactory.setCommonErrorHandler(notificationDltErrorHandler());
        return kafkaListenerContainerFactory;
    }

    // 알림용 컨슈머 팩토리 (필요 시 옵션 분리/강화 가능)
    @Bean
    public ConsumerFactory<String, Object> notificationConsumerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(org.apache.kafka.clients.consumer.ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(org.apache.kafka.clients.consumer.ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(org.apache.kafka.clients.consumer.ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        config.put(org.springframework.kafka.support.serializer.JsonDeserializer.TRUSTED_PACKAGES, "*");
        return new DefaultKafkaConsumerFactory<>(config);
    }

    // 알림 컨슈머 에러 핸들러: ".notification.DLT" 접미사로 퍼블리시
    @Bean
    public DefaultErrorHandler notificationErrorHandler(){
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(
                kafkaTemplate, (rec, ex) -> {
            log.error("[notification] DLT 전송 topic={}, 에러={}", rec.topic(), ex.getMessage());
            return new TopicPartition(rec.topic() + ".notification.DLT", rec.partition()); // ← 마침표 포함
        });

        var backoff = new ExponentialBackOff();
        backoff.setInitialInterval(1000L);  // 1초
        backoff.setMultiplier(1.5);         // 재시도 마다 시간 *1.5
        backoff.setMaxInterval(10000L);     // 재시도의 대기시간의 최대 설정 10초
        backoff.setMaxAttempts(3);

        DefaultErrorHandler h = new DefaultErrorHandler(recoverer, backoff);
        h.addNotRetryableExceptions(
                IllegalAccessException.class,               // 접근 권한이 없어 실패
                MessageConversionException.class,           // 메시지 자체가 문제 있는 경우
                MethodArgumentResolutionException.class,    // 카프카 바인딩 문제
                ClassCastException.class,                   // 타입불일치
                ValidateException.class,                    // 검증 로직 문제
                NoSuchMethodException.class,                // 존재하지않는 메서드 호출시 발생
                InvalidFormatException.class,               // 제공된 타입과 다른 형식에 타입이 들어올 때
                DeserializationException.class              // 역직렬화 실패 JSON 파싱 실패
        );
        return h;
    }

    // 알림 DLT 리스너 에러 핸들러: 재처리 실패 시 parking으로
    @Bean
    public DefaultErrorHandler notificationDltErrorHandler(){
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(
                kafkaTemplate, (rec, ex) -> {
            // ...notification.DLT → .error.parking
            String original = rec.topic().replaceFirst("\\.notification\\.DLT$", "");
            return new TopicPartition(original + ".error.parking", rec.partition());
        });

        ExponentialBackOff backoff = new ExponentialBackOff();
        backoff.setInitialInterval(5000L);  // 1초
        backoff.setMultiplier(1.5);         // 재시도 마다 시간 *1.5
        backoff.setMaxInterval(10000L);     // 재시도의 대기시간의 최대 설정 10초
        backoff.setMaxAttempts(2);

        DefaultErrorHandler h = new DefaultErrorHandler(recoverer, backoff);
        h.addNotRetryableExceptions(
                IllegalAccessException.class,               // 접근 권한이 없어 실패
                MessageConversionException.class,           // 메시지 자체가 문제 있는 경우
                MethodArgumentResolutionException.class,    // 카프카 바인딩 문제
                ClassCastException.class,                   // 타입불일치
                ValidateException.class,                    // 검증 로직 문제
                NoSuchMethodException.class,                // 존재하지않는 메서드 호출시 발생
                InvalidFormatException.class,               // 제공된 타입과 다른 형식에 타입이 들어올 때
                DeserializationException.class              // 역직렬화 실패 JSON 파싱 실패
        );
        return h;
    }

}