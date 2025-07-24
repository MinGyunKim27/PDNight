package org.example.pdnight.domain.event.repository;

import org.example.pdnight.domain.event.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long>{
}
