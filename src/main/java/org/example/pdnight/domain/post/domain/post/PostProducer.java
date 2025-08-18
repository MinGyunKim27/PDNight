package org.example.pdnight.domain.post.domain.post;

import org.example.pdnight.global.event.PostEvent;

public interface PostProducer {
    void produce(final String topic, final Object message);
    void produceAck(final String topic, final Object message);
    void producePostEvent(PostEvent event);
}
