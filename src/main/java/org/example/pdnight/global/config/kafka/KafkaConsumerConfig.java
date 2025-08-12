package org.example.pdnight.global.config.kafka;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
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
public class KafkaConsumerConfig {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public ConsumerFactory<String, Object> consumerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);

        // 보안을 생각하면 특정 패키지로 제한하는 걸 권장 (현재는 원문 유지)
        config.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        return new DefaultKafkaConsumerFactory<>(config);
    }

    // 공통 컨슈머 리스너 팩토리
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory = new ConcurrentKafkaListenerContainerFactory<>();
        kafkaListenerContainerFactory.setConsumerFactory(consumerFactory());
        kafkaListenerContainerFactory.setCommonErrorHandler(commonErrorHandler());
        return kafkaListenerContainerFactory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> dltListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory = new ConcurrentKafkaListenerContainerFactory<>();
        kafkaListenerContainerFactory.setConsumerFactory(consumerFactory());
        kafkaListenerContainerFactory.setCommonErrorHandler(dltErrorHandler());    // 에러 핸들러 주입
        return kafkaListenerContainerFactory;
    }

    // 공통 에러 핸들러: .DLT로 보냄
    @Bean
    public DefaultErrorHandler commonErrorHandler(){
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(
                kafkaTemplate, (rec, ex) -> {
            log.error("DLT 전송 메시지={}, 에러={}", rec.value(), ex.getMessage());
            return new TopicPartition(rec.topic() + ".DLT", rec.partition());
        });

        // Backoff 전략 : ExponentialBackOff 지수 백오프
        var backoff = new ExponentialBackOff();
        backoff.setInitialInterval(1000L);  // 1초
        backoff.setMultiplier(1.5);         // 재시도 마다 시간 *1.5
        backoff.setMaxInterval(10000L);     // 재시도의 대기시간의 최대 설정 10초
        backoff.setMaxAttempts(5);

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

    //dlt 컨슈머 리스너 설정
    @Bean
    public DefaultErrorHandler dltErrorHandler(){
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer
                (kafkaTemplate,(rec, e)-> {
                    return new TopicPartition(rec.topic().replace(".DLT","") + ".error.parking", rec.partition());
                });

        // Backoff 전략 : ExponentialBackOff 지수 백오프
        // 점진적으로 재시도 주기 증가
        ExponentialBackOff backOff = new ExponentialBackOff();
        backOff.setInitialInterval(5000L);  // 초기 재시도 시작 시간 5초
        backOff.setMultiplier(1.5);         // 재시도 마다 시간 *1.5
        backOff.setMaxInterval(10000L);     // 재시도의 대기시간의 최대 설정 10초
        backOff.setMaxAttempts(2);

        DefaultErrorHandler errorHandler = new DefaultErrorHandler(recoverer, backOff);

        //재시도 하더라도 똑같이 예외가 터지는 케이스 -> 재시도 처리 제외
        errorHandler.addNotRetryableExceptions(
                IllegalAccessException.class,               // 접근 권한이 없어 실패
                MessageConversionException.class,           // 메시지 자체가 문제 있는 경우
                MethodArgumentResolutionException.class,    // 카프카 바인딩 문제
                ClassCastException.class,                   // 타입불일치
                ValidateException.class,                    // 검증 로직 문제
                NoSuchMethodException.class,                // 존재하지않는 메서드 호출시 발생
                InvalidFormatException.class,               // 제공된 타입과 다른 형식에 타입이 들어올 때
                DeserializationException.class              // 역직렬화 실패 JSON 파싱 실패
        );

        return errorHandler;
    }

}