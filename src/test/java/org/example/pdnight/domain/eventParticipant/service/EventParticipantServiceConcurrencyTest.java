package org.example.pdnight.domain.eventParticipant.service;

import org.example.pdnight.domain.event.entity.Event;
import org.example.pdnight.domain.event.repository.EventRepository;
import org.example.pdnight.domain.event.service.EventService;
import org.example.pdnight.domain.eventParticipant.repository.EventParticipantRepository;
import org.example.pdnight.domain.user.entity.User;
import org.example.pdnight.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class EventParticipantServiceConcurrencyTest {
    @Autowired
    private EventParticipantRepository eventParticipantRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

    private static final int THREAD_COUNT = 10;
    private static final int MAX_PARTICIPANTS = 5;

    private Long eventId;
    @Autowired
    private EventParticipantService eventParticipantService;

    @BeforeEach
    void setUp() {
        // 사용자 10명 저장
        int i;
        for (i = 0; i < THREAD_COUNT; i++) {
            userRepository.save(new User((long) i, "user" + i));
        }

        Event event = new Event((long) i, "테스트 이벤트", MAX_PARTICIPANTS);
        eventRepository.save(event);
        eventId = event.getId();
    }

    @Test
    void 동시에_10명이_참가신청() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);

        for (int i = 0; i < THREAD_COUNT; i++) {
            final Long userId = (long) (i + 1);
            executor.submit(() -> {
                try {
                    eventParticipantService.addParticipant(eventId, userId);
                } catch (Exception e) {
                    // 무시: 실패는 자연스러운 것
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        long count = eventParticipantRepository.getEventParticipantByEventId(eventId);
        System.out.println("참가자 수: " + count);

        // 현재 동시성 제어가 없으므로 실패할 수 있음 (예: 6명 이상 들어옴)
        // 실패해야 정상
        assertThat(count).isLessThanOrEqualTo(MAX_PARTICIPANTS);
    }
}
