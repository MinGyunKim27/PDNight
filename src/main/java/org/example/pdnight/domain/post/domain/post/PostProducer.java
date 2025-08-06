package org.example.pdnight.domain.post.domain.post;

public interface PostProducer {
    void produce(final String topic, final Object message);
}
