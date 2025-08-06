package org.example.pdnight.domain.post.domain.post;

public interface PostProducer {

    void produce(String topic, Object message);

}
