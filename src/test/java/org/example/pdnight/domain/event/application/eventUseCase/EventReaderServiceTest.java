package org.example.pdnight.domain.event.application.eventUseCase;

import java.util.List;
import java.util.Optional;
import org.assertj.core.util.Lists;
import org.example.pdnight.domain.event.domain.EventReader;
import org.example.pdnight.domain.event.domain.entity.Event;
import org.example.pdnight.domain.event.domain.entity.EventParticipant;
import org.example.pdnight.domain.event.presentation.dto.response.EventParticipantResponse;
import org.example.pdnight.domain.event.presentation.dto.response.EventResponse;
import org.example.pdnight.domain.user.domain.entity.User;
import org.example.pdnight.global.common.dto.PagedResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EventReaderServiceTest {
    @InjectMocks
    private EventReaderService eventReaderService;

    @Mock
    private EventReader eventReader;

    @Test
    @DisplayName("이벤트 조회")
    void 이벤트_조회(){
        LocalDateTime fixedDateTime = LocalDateTime.of(2025, 8, 1, 12, 0);
        Event event = Event.from("title", "content", 50, fixedDateTime, fixedDateTime.plusDays(10));
        ReflectionTestUtils.setField(event, "id", 1L);

        when(eventReader.findById(1L)).thenReturn(Optional.of(event));

        EventResponse response = eventReaderService.findEventById(1L);

        assert response != null;
        assert response.getId() == 1L;
        assert response.getTitle().equals("title");
        assert response.getContent().equals("content");
        assert response.getMaxParticipants() == 50;
        assert response.getEventStartDate().equals(fixedDateTime);
        assert response.getEventEndDate().equals(fixedDateTime.plusDays(10));
    }

    @Test
    @DisplayName("이벤트 리스트 조회")
    void 이벤트_리스트_조회 () {
        LocalDateTime fixedDateTime = LocalDateTime.of(2025, 8, 1, 12, 0);
        Event event1 = Event.from("title1", "content1", 50, fixedDateTime, fixedDateTime.plusDays(10));
        ReflectionTestUtils.setField(event1, "id", 1L);
        Event event2 = Event.from("title2", "content2", 50, fixedDateTime, fixedDateTime.plusDays(10));
        ReflectionTestUtils.setField(event2, "id", 2L);

        Pageable pageable = PageRequest.of(0, 10);
        List<Event> events = Lists.newArrayList(event1, event2);
        Page<Event> page = new PageImpl<>(events, pageable, events.size());

        when(eventReader.findAllEvent(pageable)).thenReturn(page);

        PagedResponse<EventResponse> result = eventReaderService.findEventList(pageable);

        assert result != null;
        assert result.contents().get(0).getTitle().equals("title1");
        assert result.contents().get(1).getTitle().equals("title2");
    }

    @Test
    @DisplayName("이벤트 참가 신청 유저 목록 조회")
    void 이벤트_참가_신청_유저_목록(){
        Long eventId = 1L;
        Pageable pageable = PageRequest.of(0, 10);

        Event event = Event.from(
                "title", "content", 50, LocalDateTime.now(), LocalDateTime.now().plusDays(10)
        );
        User user = User.createTestUser(
                1L, "name", "name@email.com", "asdf1234!"
        );
        EventParticipant participant = EventParticipant.create(event, 1L);

        List<EventParticipant> participants = List.of(participant);
        Page<EventParticipant> page = new PageImpl<>(participants, pageable, participants.size());

        when(eventReader.findById(eventId)).thenReturn(Optional.of(event)); // 이 부분 추가
        when(eventReader.findByEventWithUser(event, pageable)).thenReturn(page);

        PagedResponse<EventParticipantResponse> result = eventReaderService.findEventParticipantList(eventId, pageable);

        assertNotNull(result);
        assertEquals(1, result.contents().size());
        assertEquals(user.getId(), result.contents().get(0).getUser().get(0)); // getUserId()가 있을 경우

    }

    @Test
    @DisplayName("내가 신청한 이벤트 목록 조회")
    void 내가_신청한_이벤트_목록(){

    }
}
