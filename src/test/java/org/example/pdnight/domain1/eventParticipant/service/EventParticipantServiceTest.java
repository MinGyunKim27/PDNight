package org.example.pdnight.domain1.eventParticipant.service;

import org.example.pdnight.global.common.helper.GetHelper;
import org.example.pdnight.domain1.event.entity.Event;
import org.example.pdnight.domain1.event.repository.EventRepository;
import org.example.pdnight.domain1.eventParticipant.entity.EventParticipant;
import org.example.pdnight.domain1.eventParticipant.repository.EventParticipantRepository;
import org.example.pdnight.domain1.user.entity.User;
import org.example.pdnight.domain1.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventParticipantServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private EventParticipantRepository eventParticipantRepository;

    @Mock
    private GetHelper helper;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private EventParticipantService eventParticipantService;

    @Test
    void 성공_참가신청() {
        // Given
        Long eventId = 1L;
        Long userId = 100L;

        Event event = mock();
        User user = mock();

        when(event.getMaxParticipants()).thenReturn(5);

        when(eventParticipantRepository.existsByEventIdAndUserId(eventId, userId)).thenReturn(false);
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(helper.getUserByIdOrElseThrow(userId)).thenReturn(user);
        when(eventParticipantRepository.getEventParticipantByEventId(eventId)).thenReturn(3);

        // When
        eventParticipantService.addParticipant(eventId, userId);

        // Then
        verify(eventParticipantRepository, times(1)).save(any(EventParticipant.class));
    }
}
