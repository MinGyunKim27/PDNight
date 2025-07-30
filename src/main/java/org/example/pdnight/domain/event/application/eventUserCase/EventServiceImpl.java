package org.example.pdnight.domain.event.application.eventUserCase;

import org.example.pdnight.domain.common.dto.PagedResponse;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.pdnight.domain.common.enums.ErrorCode;
import org.example.pdnight.domain.common.exception.BaseException;
import org.example.pdnight.domain.event.presentation.dto.request.EventCreateRequest;
import org.example.pdnight.domain.event.presentation.dto.response.EventResponse;
import org.example.pdnight.domain.event.domain.entity.Event;
import org.example.pdnight.domain.event.infra.EventJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventServiceImpl {

    private final EventJpaRepository eventJpaRepository;

    // 이벤트 생성
    @Transactional
    public EventResponse createEvent(EventCreateRequest request){
        Event event = Event.from(
                request.getTitle(),
                request.getContent(),
                request.getMaxParticipants(),
                request.getEventDate()
        );

        eventJpaRepository.save(event);
        return EventResponse.from(event);
    }

    // 이벤트 조회
    @Transactional(readOnly = true)
    public EventResponse findEventById(Long id){
        Event event = getEventById(id);

        return EventResponse.from(event);
    }

    @Transactional(readOnly = true)
    public PagedResponse<EventResponse> findEventList(Pageable pageable) {
        Page<Event> eventPage = eventJpaRepository.findAll(pageable);
        return PagedResponse.from(eventPage.map(EventResponse::from));
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

        eventJpaRepository.save(event);
        return EventResponse.from(event);
    }

    // 이벤트 삭제
    @Transactional
    public void deleteEventById(Long id){
        Event event = getEventById(id);
        eventJpaRepository.delete(event);
    }


    // ----------------------------------- HELPER 메서드 ------------------------------------------------------ //
    // get
    private Event getEventById(Long id) {
        return eventJpaRepository.findById(id).orElseThrow(
                () -> new BaseException(ErrorCode.EVENT_NOT_FOUNT)
        );
    }
}

/* -------------- 이벤트 참가 서비스 합쳐야함 --------------------
@Service
@RequiredArgsConstructor
public class EventParticipantService {

    private final EventParticipantRepository eventParticipantRepository;
    private final EventRepository eventRepository;
    private final GetHelper helper;

    // 참가 신청
    @Transactional
    @DistributedLock(
            key = "#eventId",
            timeoutMs = 5000,
            leaseTimeMs = 3000 // 락 유지 시간
    )
    public void addParticipant(Long eventId, Long userId){
        // 이미 참가 신청한 유저이면 실패
        validateParticipant(eventId, userId);

        Event event = getEventById(eventId);
        User user = helper.getUserByIdOrElseThrow(userId);

        // 신청 인원 확인
        int participantsCount = eventParticipantRepository.getEventParticipantByEventId(eventId);
        if(participantsCount == event.getMaxParticipants()){
            throw new BaseException(ErrorCode.EVENT_PARTICIPANT_FULL);
        }

        EventParticipant eventParticipant = EventParticipant.create(event, user);
        eventParticipantRepository.save(eventParticipant);
    }

    // 참가 신청 유저 목록 조회
    public PagedResponse<EventParticipantResponse> findEventParticipantList(Long eventId, Pageable pageable) {
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new BaseException(ErrorCode.EVENT_NOT_FOUNT)
        );
        Page<EventParticipant> eventPage = eventParticipantRepository.findByEventWithUser(event, pageable);
        return PagedResponse.from(eventPage.map(EventParticipantResponse::from));
    }


    // ----------------------------------- HELPER 메서드 ------------------------------------------------------ //
    // get
    private Event getEventById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new BaseException(ErrorCode.EVENT_NOT_FOUNT));
    }

    // validate
    private void validateParticipant(Long eventId, Long userId) {
        if (eventParticipantRepository.existsByEventIdAndUserId(eventId, userId)) {
            throw new BaseException(ErrorCode.EVENT_ALREADY_PENDING);
        }
    }

    private void validateIsFullEvent(int participantsCount, Event event) {
        if (participantsCount >= event.getMaxParticipants()) {
            throw new BaseException(ErrorCode.EVENT_PARTICIPANT_FULL);
        }
    }

}
 */
