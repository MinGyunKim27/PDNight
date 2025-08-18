package org.example.pdnight.domain.user.infra.userInfra;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.user.domain.userDomain.UserProducer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class UserProducerImpl implements UserProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final KafkaTemplate<String, Object> kafkaAckTemplate;

    public UserProducerImpl(
            KafkaTemplate<String, Object> kafkaTemplate,
            @Qualifier("kafkaAckTemplate") KafkaTemplate<String, Object> kafkaAckTemplate
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.kafkaAckTemplate = kafkaAckTemplate;
    }

    @Override
    public void produce(final String topic, final Object message) {
        kafkaTemplate.send(topic, message);
    }

    @Override
    public void produceAck(final String topic, final Object message) {
        kafkaAckTemplate.executeInTransaction(producer -> {
            producer.send(topic, message);
            return true;
        });
    }
}
