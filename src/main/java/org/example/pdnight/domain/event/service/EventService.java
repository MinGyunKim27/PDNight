package org.example.pdnight.domain.event.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.pdnight.domain.common.enums.ErrorCode;
import org.example.pdnight.domain.common.exception.BaseException;
import org.example.pdnight.domain.event.dto.request.EventCreateRequest;
import org.example.pdnight.domain.event.dto.response.EventResponse;
import org.example.pdnight.domain.event.entity.Event;
import org.example.pdnight.domain.event.repository.EventRepository;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;

    // 이벤트 생성
    @Transactional
    public EventResponse createEvent(EventCreateRequest request){
        Event event = new Event(
                request.getTitle(),
                request.getContent(),
                request.getMaxParticipants(),
                request.getEventDate()
        );

        eventRepository.save(event);
        return new EventResponse(event);
    }

    // 이벤트 조회
    public EventResponse findEventById(Long id){
        Event event = eventRepository.findById(id).orElseThrow(
                () -> new BaseException(ErrorCode.EVENT_NOT_FOUNT)
        );

        return new EventResponse(event);
    }

    // 이벤트 수정
    @Transactional
    public EventResponse updateEvent(Long id, EventCreateRequest request){
        Event event = eventRepository.findById(id).orElseThrow(
                () -> new BaseException(ErrorCode.EVENT_NOT_FOUNT)
        );

        event.updateEvent(
                request.getTitle(),
                request.getContent(),
                request.getMaxParticipants(),
                request.getEventDate()
        );

        eventRepository.save(event);
        return new EventResponse(event);
    }
}
