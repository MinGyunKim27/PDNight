package org.example.pdnight.domain.user.domain.userDomain;

public interface UserProducer {
    void produce(final String topic, final Object message);
}
