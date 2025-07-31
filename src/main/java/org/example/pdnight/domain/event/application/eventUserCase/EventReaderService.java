package org.example.pdnight.domain.event.application.eventUserCase;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.common.dto.PagedResponse;
import org.example.pdnight.domain.common.enums.ErrorCode;
import org.example.pdnight.domain.common.exception.BaseException;
import org.example.pdnight.domain.event.domain.EventReader;
import org.example.pdnight.domain.event.domain.entity.Event;
import org.example.pdnight.domain.event.domain.entity.EventParticipant;
import org.example.pdnight.domain.event.presentation.dto.response.EventParticipantResponse;
import org.example.pdnight.domain.event.presentation.dto.response.EventResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EventReaderService {

    private final EventReader eventReader;

    @Transactional(readOnly = true)
    public EventResponse findEventById(Long id) {
        Event event = getEventById(id);

        return EventResponse.from(event);
    }

    @Transactional(readOnly = true)
    public PagedResponse<EventResponse> findEventList(Pageable pageable) {
        Page<Event> eventPage = eventReader.findAllEvent(pageable);
        return PagedResponse.from(eventPage.map(EventResponse::from));
    }

    // 참가 신청 유저 목록 조회
    public PagedResponse<EventParticipantResponse> findEventParticipantList(Long eventId, Pageable pageable) {
        Event event = getEventById(eventId);
        Page<EventParticipant> eventPage = eventReader.findByEventWithUser(event, pageable);
        return PagedResponse.from(eventPage.map(EventParticipantResponse::from));
    }


    // ----------------------------------- HELPER 메서드 ------------------------------------------------------ //
    // get
    private Event getEventById(Long id) {
        return eventReader.findById(id).orElseThrow(
                () -> new BaseException(ErrorCode.EVENT_NOT_FOUNT)
        );
    }

    // validate
    private void validateParticipant(Long eventId, Long userId) {
        if (eventReader.existsEventByIdAndUserId(eventId, userId)) {
            throw new BaseException(ErrorCode.EVENT_ALREADY_PENDING);
        }
    }

    private void validateIsFullEvent(int participantsCount, Event event) {
        if (participantsCount >= event.getMaxParticipants()) {
            throw new BaseException(ErrorCode.EVENT_PARTICIPANT_FULL);
        }
    }
}
