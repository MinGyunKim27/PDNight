package org.example.pdnight.domain.auth.infra;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.auth.domain.AuthProducer;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthProducerImpl implements AuthProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void produce(final String topic, final Object message) {
        kafkaTemplate.send(topic, message);
    }
}
