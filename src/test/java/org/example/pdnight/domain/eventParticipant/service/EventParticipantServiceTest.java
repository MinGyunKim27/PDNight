package org.example.pdnight.domain.eventParticipant.service;

import org.example.pdnight.domain.event.entity.Event;
import org.example.pdnight.domain.event.repository.EventRepository;
import org.example.pdnight.domain.eventParticipant.entity.EventParticipant;
import org.example.pdnight.domain.eventParticipant.repository.EventParticipantRepository;
import org.example.pdnight.domain.user.entity.User;
import org.example.pdnight.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventParticipantServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private EventParticipantRepository eventParticipantRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private EventParticipantService eventParticipantService;

    @Test
    void 성공_참가신청() {
        // Given
        Long eventId = 1L;
        Long userId = 100L;

        Event event = new Event(eventId, 5);
        User user = new User(userId, "Test");

        when(eventParticipantRepository.existsByEventIdAndUserId(eventId, userId)).thenReturn(false);
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(eventParticipantRepository.getEventParticipantByEventId(eventId)).thenReturn(3);

        // When
        eventParticipantService.addParticipant(eventId, userId);

        // Then
        verify(eventParticipantRepository, times(1)).save(any(EventParticipant.class));
    }
}

