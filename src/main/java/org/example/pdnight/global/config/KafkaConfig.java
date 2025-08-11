package org.example.pdnight.global.config;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.RoundRobinPartitioner;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.security.oauthbearer.internals.secured.ValidateException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.DeserializationException;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.messaging.converter.MessageConversionException;
import org.springframework.messaging.handler.invocation.MethodArgumentResolutionException;
import org.springframework.util.backoff.ExponentialBackOff;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
@Slf4j
public class KafkaConfig {

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate(){
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    @Qualifier("kafkaAckTemplate")
    public KafkaTemplate<String, Object> kafkaAckTemplate(){
        return new KafkaTemplate<>(producerAckFactory());
    }

    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                "127.0.0.1:10000,127.0.0.1:10001,127.0.0.1:10002");
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        //        파티션 자동 분배
        config.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, RoundRobinPartitioner.class.getName());

        config.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, 1000);// 재시도 간 간격
        config.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, 10000); // 전송 전체 제한 시간
        config.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, 1000); // 서버 응답까지 기다릴 시간
        config.put(ProducerConfig.RETRIES_CONFIG, "5"); // 최대 재시도 횟수
        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean // 회원 가입에 사용
    public ProducerFactory<String, Object> producerAckFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                "127.0.0.1:10000,127.0.0.1:10001,127.0.0.1:10002");
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        //        파티션 자동 분배
        config.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, RoundRobinPartitioner.class.getName());

        // 정확한 전송 한번만 수행
        config.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true); // 중복 전송 방지
        config.put(ProducerConfig.ACKS_CONFIG, "all"); // 모든 브로커에게 확인 받을때까지 대기 (0은 대기 X, 1은 메인 브로커 응답, all은 모든 브로커 응답)
        config.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, "5"); // 정확히 한번 전송을 위한 설정
        config.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, 1000);// 재시도 간 간격
        config.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, 20000); // 전송 전체 제한 시간
        config.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, 1000); // 서버 응답까지 기다릴 시간
        config.put(ProducerConfig.RETRIES_CONFIG, "5"); // 최대 재시도 횟수
        config.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, 20000); // 트랜잭션용 프로듀서 ID를 얻기 위해 기다리는 시간 20초로 설정

        DefaultKafkaProducerFactory<String, Object> factory = new DefaultKafkaProducerFactory<>(config);
        factory.setTransactionIdPrefix("tx-producer-"); // 트랜잭션 producer 설정
        return factory;
    }

    /**
     * 컨수머 팩토리 설정<br>
     * 추후에 추가 되면 메서드가 하나씩 추가 되어야 할 듯.
     */
    @Bean
    public ConsumerFactory<String, Object> consumerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                "127.0.0.1:10000,127.0.0.1:10001,127.0.0.1:10002");
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);

        config.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        return new DefaultKafkaConsumerFactory<>(config);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory = new ConcurrentKafkaListenerContainerFactory<>();
        kafkaListenerContainerFactory.setConsumerFactory(consumerFactory());
        kafkaListenerContainerFactory.setCommonErrorHandler(errorHandler());    // 에러 핸들러 주입
        return kafkaListenerContainerFactory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> notificationListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory = new ConcurrentKafkaListenerContainerFactory<>();
        kafkaListenerContainerFactory.setConsumerFactory(consumerFactory());
        kafkaListenerContainerFactory.setCommonErrorHandler(notificationErrorHandler());    // 에러 핸들러 주입
        return kafkaListenerContainerFactory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> dltListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory = new ConcurrentKafkaListenerContainerFactory<>();
        kafkaListenerContainerFactory.setConsumerFactory(consumerFactory());
        kafkaListenerContainerFactory.setCommonErrorHandler(dltErrorHandler());    // 에러 핸들러 주입
        return kafkaListenerContainerFactory;
    }

    //기본 컨슈머 리스너 설정
    @Bean
    public DefaultErrorHandler errorHandler(){
        // 일정 재시도 횟수 초과시 dead-letter-topic 으로 등록
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer
                (kafkaTemplate(),(consumerRecord, e)-> {
                    log.error("DLT 전송 메시지={}, 에러={}", consumerRecord.value(), e.getMessage());
                    return new TopicPartition(consumerRecord.topic() + ".DLT", consumerRecord.partition());
                });

        // Backoff 전략 : ExponentialBackOff 지수 백오프
        // 점진적으로 재시도 주기 증가
        ExponentialBackOff backOff = new ExponentialBackOff();
        backOff.setInitialInterval(1000L);  // 1초
        backOff.setMultiplier(1.5);         // 재시도 마다 시간 *1.5
        backOff.setMaxInterval(10000L);     // 재시도의 대기시간의 최대 설정 10초
        backOff.setMaxAttempts(5);

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

    //dlt 컨슈머 리스너 설정
    @Bean
    public DefaultErrorHandler dltErrorHandler(){
        // 일정 재시도 횟수 초과시 dead-letter-topic 으로 등록
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer
                (kafkaTemplate(),(consumerRecord, e)-> {
                    //log.error("DLT 전송 메시지={}, 에러={}", consumerRecord.value(), e.getMessage());
                    return new TopicPartition(consumerRecord.topic().replace(".DLT","") + ".error.parking", consumerRecord.partition());
                });

        // Backoff 전략 : ExponentialBackOff 지수 백오프
        // 점진적으로 재시도 주기 증가
        ExponentialBackOff backOff = new ExponentialBackOff();
        backOff.setInitialInterval(5000L);  // 1초
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

    //알림 컨슈머 리스너 설정
    @Bean
    public DefaultErrorHandler notificationErrorHandler(){
        // 일정 재시도 횟수 초과시 dead-letter-topic 으로 등록
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer
                (kafkaTemplate(),(consumerRecord, e)-> {
                    log.error("DLT 전송 메시지={}, 에러={}", consumerRecord.value(), e.getMessage());
                    return new TopicPartition(consumerRecord.topic() + "notification.DLT", consumerRecord.partition());
                });

        // Backoff 전략 : ExponentialBackOff 지수 백오프
        // 점진적으로 재시도 주기 증가
        ExponentialBackOff backOff = new ExponentialBackOff();
        backOff.setInitialInterval(1000L);  // 1초
        backOff.setMultiplier(1.5);         // 재시도 마다 시간 *1.5
        backOff.setMaxAttempts(3);

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