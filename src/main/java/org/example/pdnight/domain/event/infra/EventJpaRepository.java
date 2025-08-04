package org.example.pdnight.domain.event.infra;

import org.example.pdnight.domain.event.domain.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventJpaRepository extends JpaRepository<Event, Long>{
}
