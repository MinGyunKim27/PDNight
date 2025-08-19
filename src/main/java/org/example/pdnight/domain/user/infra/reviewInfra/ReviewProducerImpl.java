package org.example.pdnight.domain.user.infra.reviewInfra;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.user.domain.reviewDomain.ReviewProducer;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReviewProducerImpl implements ReviewProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void produce(String topic, Object message) {
        kafkaTemplate.send(topic, message);
    }
}
