package org.example.pdnight.domain.post.infra.post;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void produce(final String topic, final Object message) {
        kafkaTemplate.send(topic, message);
    }
}
