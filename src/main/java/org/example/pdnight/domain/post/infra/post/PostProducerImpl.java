package org.example.pdnight.domain.post.infra.post;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.post.domain.post.PostProducer;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostProducerImpl implements PostProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void produce(final String topic, final Object message) {
        kafkaTemplate.send(topic, message);
    }
}
