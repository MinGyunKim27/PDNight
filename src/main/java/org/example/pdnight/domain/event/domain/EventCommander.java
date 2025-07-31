package org.example.pdnight.domain.event.domain;

import org.example.pdnight.domain.event.domain.entity.Event;

public interface EventCommander {

    Event save(Event event);

    void delete(Event event);
}
