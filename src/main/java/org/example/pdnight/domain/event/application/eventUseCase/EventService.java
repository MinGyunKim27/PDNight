package org.example.pdnight.domain.event.application.eventUseCase;

import org.example.pdnight.domain.event.presentation.dto.request.EventCreateRequest;
import org.example.pdnight.domain.event.presentation.dto.response.EventParticipantResponse;
import org.example.pdnight.domain.event.presentation.dto.response.EventResponse;
import org.example.pdnight.global.common.dto.PagedResponse;
import org.springframework.data.domain.Pageable;

public interface EventService {
    EventResponse createEvent(EventCreateRequest request);

    EventResponse findEventById(Long id);

    PagedResponse<EventResponse> findEventList(Pageable pageable);

    EventResponse updateEvent(Long id, EventCreateRequest request);

    void deleteEventById(Long id);

    void addParticipant(Long eventId, Long userId);

    PagedResponse<EventParticipantResponse> findEventParticipantList(Long eventId, Pageable pageable);

    PagedResponse<EventResponse> findMyParticipantEvents(Long userId, Pageable pageable);
}
