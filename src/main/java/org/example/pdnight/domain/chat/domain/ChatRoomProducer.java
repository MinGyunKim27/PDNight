package org.example.pdnight.domain.chat.domain;

public interface ChatRoomProducer {
    void produce(final String topic, final Object message);
}
