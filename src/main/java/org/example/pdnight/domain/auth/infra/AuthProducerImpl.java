package org.example.pdnight.domain.auth.infra;

import lombok.extern.slf4j.Slf4j;
import org.example.pdnight.domain.auth.domain.AuthProducer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AuthProducerImpl implements AuthProducer {
    private final KafkaTemplate<String, Object> kafkaAckTemplate;

    public AuthProducerImpl(@Qualifier("kafkaAckTemplate") KafkaTemplate<String, Object> kafkaAckTemplate) {
        this.kafkaAckTemplate = kafkaAckTemplate;
    }

    @Override
    public void produce(final String topic, final Object message) {
        kafkaAckTemplate.executeInTransaction(producer -> {
            producer.send(topic, message);
            return true;
        });
    }

}