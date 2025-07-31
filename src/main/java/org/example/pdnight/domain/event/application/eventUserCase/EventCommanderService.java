package org.example.pdnight.domain.event.application.eventUserCase;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.common.enums.ErrorCode;
import org.example.pdnight.domain.common.exception.BaseException;
import org.example.pdnight.domain.event.domain.EventCommander;
import org.example.pdnight.domain.event.domain.EventReader;
import org.example.pdnight.domain.event.domain.entity.Event;
import org.example.pdnight.domain.event.domain.entity.EventParticipant;
import org.example.pdnight.domain.event.presentation.dto.request.EventCreateRequest;
import org.example.pdnight.domain.event.presentation.dto.response.EventResponse;
import org.example.pdnight.global.aop.DistributedLock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EventCommanderService {

    private final EventCommander eventCommander;
    private final EventReader eventReader;

    // 이벤트 생성
    @Transactional
    public EventResponse createEvent(EventCreateRequest request) {
        Event event = Event.from(
                request.getTitle(),
                request.getContent(),
                request.getMaxParticipants(),
                request.getEventDate()
        );

        Event saveEvent = eventCommander.save(event);
        return EventResponse.from(saveEvent);
    }

    @Transactional
    public EventResponse updateEvent(Long id, EventCreateRequest request) {
        Event event = getEventById(id);

        event.updateEvent(
                request.getTitle(),
                request.getContent(),
                request.getMaxParticipants(),
                request.getEventDate()
        );

        Event saveEvent = eventCommander.save(event);
        return EventResponse.from(saveEvent);
    }

    @Transactional
    public void deleteEventById(Long id) {
        Event event = getEventById(id);
        eventCommander.delete(event);
    }

    @Transactional
    @DistributedLock(
            key = "#eventId",
            timeoutMs = 5000
    )
    public void addParticipant(Long eventId, Long userId) {
        // 이미 참가 신청한 유저이면 실패
        validateParticipant(eventId, userId);

        Event event = getEventById(eventId);

        // 신청 인원 확인
        Long participantsCount = eventReader.getEventParticipantByEventId(eventId);
        if (participantsCount.intValue() == event.getMaxParticipants()) {
            throw new BaseException(ErrorCode.EVENT_PARTICIPANT_FULL);
        }

        EventParticipant eventParticipant = EventParticipant.create(event, userId);
        event.addParticipant(eventParticipant);
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
