package org.example.pdnight.domain.event.service;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.pdnight.domain.common.enums.ErrorCode;
import org.example.pdnight.domain.common.exception.BaseException;
import org.example.pdnight.domain.event.dto.request.EventCreateRequest;
import org.example.pdnight.domain.event.dto.response.EventResponse;
import org.example.pdnight.domain.event.entity.Event;
import org.example.pdnight.domain.event.repository.EventRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
        return EventResponse.from(event);
    }

    // 이벤트 조회
    @Transactional(readOnly = true)
    public EventResponse findEventById(Long id){
        Event event = getEventById(id);

        return new EventResponse(event);
    }

    @Transactional(readOnly = true)
    public Page<EventResponse> findEventList(Pageable pageable) {
        Page<Event> eventPage = eventRepository.findAll(pageable);
        return eventPage.map(EventResponse::from);
    }

    // 이벤트 수정
    @Transactional
    public EventResponse updateEvent(Long id, EventCreateRequest request){
        Event event = getEventById(id);

        event.updateEvent(
                request.getTitle(),
                request.getContent(),
                request.getMaxParticipants(),
                request.getEventDate()
        );

        eventRepository.save(event);
        return EventResponse.from(event);
    }

    // 이벤트 삭제
    @Transactional
    public void deleteEventById(Long id){
        Event event = getEventById(id);
        eventRepository.delete(event);
    }


    // ----------------------------------- HELPER 메서드 ------------------------------------------------------ //
    // get
    private Event getEventById(Long id) {
        return eventRepository.findById(id).orElseThrow(
                () -> new BaseException(ErrorCode.EVENT_NOT_FOUNT)
        );
    }
}
