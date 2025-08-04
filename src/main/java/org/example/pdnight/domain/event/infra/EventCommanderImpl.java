package org.example.pdnight.domain.event.infra;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.event.domain.EventCommander;
import org.example.pdnight.domain.event.domain.entity.Event;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class EventCommanderImpl implements EventCommander {

    private final EventJpaRepository eventJpaRepository;

    @Override
    public Event save(Event event) {
        return eventJpaRepository.save(event);
    }

    @Override
    public void delete(Event event) {
        eventJpaRepository.delete(event);
    }
}
