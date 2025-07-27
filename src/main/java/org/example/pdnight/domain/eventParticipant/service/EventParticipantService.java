package org.example.pdnight.domain.eventParticipant.service;

import jakarta.transaction.Transactional;
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
import org.example.pdnight.domain.user.repository.UserRepository;
import org.example.pdnight.global.aop.DistributedLock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.redisson.api.RedissonClient;

@Service
@RequiredArgsConstructor
public class EventParticipantService {

    private final EventParticipantRepository eventParticipantRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
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
        User user = helper.getUserById(userId);

        // 신청 인원 확인
        int participantsCount = eventParticipantRepository.getEventParticipantByEventIdWithLock(eventId);
        if(participantsCount == event.getMaxParticipants()){
            throw new BaseException(ErrorCode.EVENT_PARTICIPANT_FULL);
        }

        EventParticipant eventParticipant = new EventParticipant(event, user);
        eventParticipantRepository.save(eventParticipant);
    }

    // 참가 신청 유저 목록 조회
    public Page<EventParticipantResponse> findEventParticipantList(Long eventId, Pageable pageable) {
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new BaseException(ErrorCode.EVENT_NOT_FOUNT)
        );

        Page<EventParticipant> eventPage = eventParticipantRepository.findByEventWithUser(event, pageable);
        return eventPage.map(EventParticipantResponse::new);
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
