package org.example.pdnight.domain.post.infra.comment;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.post.domain.comment.CommentProducer;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentProducerImpl implements CommentProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void produce(String topic, Object message) {
        kafkaTemplate.send(topic, message);
    }
}
