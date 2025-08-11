package org.example.pdnight.global.config.kafka;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.RoundRobinPartitioner;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class KafkaProducerConfig {

    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                "127.0.0.1:10000,127.0.0.1:10001,127.0.0.1:10002");
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        // 파티션 자동 분배
        config.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, RoundRobinPartitioner.class.getName());

        config.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, 1000);
        config.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, 10000);
        config.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, 1000);
        config.put(ProducerConfig.RETRIES_CONFIG, 5);
        return new DefaultKafkaProducerFactory<>(config);
    }

    // 정확한 전송(멱등/ACK all) 템플릿이 필요할 때
    @Bean
    public ProducerFactory<String, Object> producerAckFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                "127.0.0.1:10000,127.0.0.1:10001,127.0.0.1:10002");
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        config.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, RoundRobinPartitioner.class.getName());
        config.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true); // 중복 전송 방지
        config.put(ProducerConfig.ACKS_CONFIG, "all");// 모든 브로커에게 확인 받을때까지 대기 (0은 대기 X, 1은 메인 브로커 응답, all은 모든 브로커 응답)
        config.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 5);// 한 연결에서 동시에 처리할 수 있는 최대 요청 수
        config.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, 1000);// 재시도 간 간격
        config.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, 20000); // 전송 전체 제한 시간
        config.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, 1000); // 서버 응답까지 기다릴 시간
        config.put(ProducerConfig.RETRIES_CONFIG, 5);   // 최대 재시도 횟수
        config.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, 20000);  // 트랜잭션용 프로듀서 ID를 얻기 위해 기다리는 시간 20초로 설정

        DefaultKafkaProducerFactory<String, Object> factory = new DefaultKafkaProducerFactory<>(config);
        factory.setTransactionIdPrefix("tx-producer-");     // 트랜잭션 producer 설정
        return factory;
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate(){
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    @Qualifier("kafkaAckTemplate")
    public KafkaTemplate<String, Object> kafkaAckTemplate(){
        return new KafkaTemplate<>(producerAckFactory());
    }

}