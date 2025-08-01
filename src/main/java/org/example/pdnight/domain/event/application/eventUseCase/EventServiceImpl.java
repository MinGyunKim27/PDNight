package org.example.pdnight.domain.event.application.eventUseCase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.pdnight.domain.event.presentation.dto.request.EventCreateRequest;
import org.example.pdnight.domain.event.presentation.dto.response.EventParticipantResponse;
import org.example.pdnight.domain.event.presentation.dto.response.EventResponse;
import org.example.pdnight.global.common.dto.PagedResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventCommanderService eventCommanderService;
    private final EventReaderService eventReaderService;

    @Transactional
    public EventResponse createEvent(EventCreateRequest request) {
        return eventCommanderService.createEvent(request);
    }

    // 이벤트 조회
    @Override
    public EventResponse findEventById(Long id) {
        return eventReaderService.findEventById(id);
    }

    @Override
    public PagedResponse<EventResponse> findEventList(Pageable pageable) {
        return eventReaderService.findEventList(pageable);
    }

    // 이벤트 수정
    @Override
    public EventResponse updateEvent(Long id, EventCreateRequest request) {
        return eventCommanderService.updateEvent(id, request);
    }

    // 이벤트 삭제
    @Override
    public void deleteEventById(Long id) {
        eventCommanderService.deleteEventById(id);
    }

    // 참가 신청
    @Override
    public void addParticipant(Long eventId, Long userId) {
        eventCommanderService.addParticipant(eventId, userId);
    }

    // 참가 신청 유저 목록 조회
    @Override
    public PagedResponse<EventParticipantResponse> findEventParticipantList(Long eventId, Pageable pageable) {
        return eventReaderService.findEventParticipantList(eventId, pageable);
    }

    @Override
    public PagedResponse<EventResponse> findMyParticipantEvents(Long userId, Pageable pageable) {
        return eventReaderService.findMyParticipantEvents(userId, pageable);
    }
}
