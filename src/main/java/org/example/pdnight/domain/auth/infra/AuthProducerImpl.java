package org.example.pdnight.domain.auth.infra;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.pdnight.domain.auth.domain.AuthProducer;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class AuthProducerImpl implements AuthProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void produce(final String topic, final Object message) {
        try {
            kafkaTemplate.send(topic, message)
                    .whenComplete((result, ex) -> {
                        // 샌드가 성공한후 카프카에 메세지 전달하는 비동기 처리에 대한 결과
                        if (ex != null) {
                            log.error("Kafka 전송 실패: {}", ex.getMessage());
                        } else {
                            log.error("Kafka 전송 성공");
                        }
                    });
        } catch (Exception e) {
            // send 자체가 실패했을때 예외처리
            log.error("send fail : {}",e.getMessage());
        }
    }
}
