package org.example.pdnight.domain.eventParticipant.service;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.common.enums.ErrorCode;
import org.example.pdnight.domain.common.exception.BaseException;
import org.example.pdnight.domain.event.dto.response.EventResponse;
import org.example.pdnight.domain.event.entity.Event;
import org.example.pdnight.domain.event.repository.EventRepository;
import org.example.pdnight.domain.event.service.EventService;
import org.example.pdnight.domain.eventParticipant.dto.response.EventParticipantResponse;
import org.example.pdnight.domain.eventParticipant.entity.EventParticipant;
import org.example.pdnight.domain.eventParticipant.repository.EventParticipantRepository;
import org.example.pdnight.domain.user.entity.User;
import org.example.pdnight.domain.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventParticipantService {

    private final EventParticipantRepository eventParticipantRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    // 참가 신청
    public void addParticipant(Long eventId, Long userId){
        // 이미 참가 신청한 유저이면 실패
        if(eventParticipantRepository.existsByEventIdAndUserId(eventId, userId)){
            throw new BaseException(ErrorCode.EVENT_ALREADY_PENDING);
        }

        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new BaseException(ErrorCode.EVENT_NOT_FOUNT)
        );
        User user = userRepository.findById(userId).orElseThrow(
                () -> new BaseException(ErrorCode.USER_NOT_FOUND)
        );

        int participantsCount = eventParticipantRepository.getEventParticipantByEventId(eventId);
        if(participantsCount >= event.getMaxParticipants()){
            throw new BaseException(ErrorCode.EVENT_PARTICIPANT_FULL);
        }

        EventParticipant eventParticipant = new EventParticipant(event, user);
        eventParticipantRepository.save(eventParticipant);
    }

    // 참가 신청 유저 목록 조회
    public Page<EventParticipantResponse> findEventParticipantList(Long eventId, Pageable pageable){
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new BaseException(ErrorCode.EVENT_NOT_FOUNT)
        );

        Page<EventParticipant> eventPage = eventParticipantRepository.findByEventIdWithUser(eventId, pageable);
        return eventPage.map(EventParticipantResponse::new);
    }
}
