package org.example.pdnight.domain.eventParticipant.service;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.common.enums.ErrorCode;
import org.example.pdnight.domain.common.exception.BaseException;
import org.example.pdnight.domain.common.helper.GetHelper;
import org.example.pdnight.domain.event.entity.Event;
import org.example.pdnight.domain.event.repository.EventRepository;
import org.example.pdnight.domain.eventParticipant.dto.response.EventParticipantResponse;
import org.example.pdnight.domain.eventParticipant.entity.EventParticipant;
import org.example.pdnight.domain.eventParticipant.repository.EventParticipantRepository;
import org.example.pdnight.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventParticipantService {

    private final EventParticipantRepository eventParticipantRepository;
    private final EventRepository eventRepository;
    private final GetHelper helper;

    // 참가 신청
    public void addParticipant(Long eventId, Long userId) {
        // 이미 참가 신청한 유저이면 실패
        validateParticipant(eventId, userId);

        Event event = getEventById(eventId);
        User user = helper.getUserById(userId);

        int participantsCount = eventParticipantRepository.getEventParticipantByEventId(eventId);

        // 참가가 다 찼는지 확인
        validateIsFullEvent(participantsCount, event);

        EventParticipant eventParticipant = new EventParticipant(event, user);
        eventParticipantRepository.save(eventParticipant);
    }

    // 참가 신청 유저 목록 조회
    public Page<EventParticipantResponse> findEventParticipantList(Long eventId, Pageable pageable) {
        Page<EventParticipant> eventPage = eventParticipantRepository.findByEventIdWithUser(eventId, pageable);
        return eventPage.map(EventParticipantResponse::from);
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
