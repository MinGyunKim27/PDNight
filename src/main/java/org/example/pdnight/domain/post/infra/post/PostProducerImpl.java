package org.example.pdnight.domain.post.infra.post;

import lombok.extern.slf4j.Slf4j;
import org.example.pdnight.domain.post.domain.post.PostProducer;
import org.example.pdnight.global.event.PostEvent;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PostProducerImpl implements PostProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final KafkaTemplate<String, Object> kafkaAckTemplate;

    public PostProducerImpl(
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

    public void producePostEvent(PostEvent event) {
        log.info("producePostEvent: {}", event);
        kafkaTemplate.send("post", event.document().getId().toString(), event);
    }

}