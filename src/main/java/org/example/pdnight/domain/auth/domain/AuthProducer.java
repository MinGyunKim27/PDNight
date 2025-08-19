package org.example.pdnight.domain.auth.domain;

public interface AuthProducer {
    void produce(final String topic, final Object message);
}
