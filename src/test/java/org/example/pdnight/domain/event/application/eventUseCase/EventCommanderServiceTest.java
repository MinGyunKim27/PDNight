package org.example.pdnight.domain.event.application.eventUseCase;

import org.example.pdnight.domain.event.domain.EventCommander;
import org.example.pdnight.domain.event.domain.EventReader;
import org.example.pdnight.domain.event.domain.entity.Event;
import org.example.pdnight.domain.event.presentation.dto.request.EventCreateRequest;
import org.example.pdnight.domain.event.presentation.dto.response.EventResponse;
import org.example.pdnight.global.common.exception.BaseException;
import org.junit.jupiter.api.DisplayName;
import org.example.pdnight.global.common.enums.ErrorCode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import java.time.LocalDateTime;
import java.util.Optional;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventCommanderServiceTest {

    @InjectMocks
    private EventCommanderService eventCommanderService;

    @Mock
    private EventCommander eventCommander;

    @Mock
    private EventReader eventReader;

    @Test
    @DisplayName("이벤트 생성 확인 테스트")
    void 이벤트_생성_성공() {
        LocalDateTime fixedDateTime = LocalDateTime.of(2025, 9, 20, 12, 0);

        EventCreateRequest request = EventCreateRequest.from(
                "title", "content", 50, fixedDateTime, fixedDateTime.plusDays(10)
        );
        Event event = Event.from(
                request.getTitle(),
                request.getContent(),
                request.getMaxParticipants(),
                request.getEventStartDate(),
                request.getEventEndDate()
        );
        // 테스트용 id 직접 세팅
        ReflectionTestUtils.setField(event, "id", 1L);

        when(eventCommander.save(any(Event.class))).thenReturn(event);

        EventResponse response = eventCommanderService.createEvent(request);

        assertThat(response.getTitle()).isEqualTo("title");
        assertThat(response.getContent()).isEqualTo("content");
        assertThat(response.getMaxParticipants()).isEqualTo(50);
        assertThat(response.getEventStartDate()).isEqualTo(fixedDateTime);
        assertThat(response.getEventEndDate()).isEqualTo(fixedDateTime.plusDays(10));
    }

    @Test
    @DisplayName("이벤트 생성 실패 - 이상한 날짜")
    void 이벤트_생성_실패_날짜_오류(){
        LocalDateTime fixedDateTime = LocalDateTime.of(2023, 6, 1, 12, 0);
        EventCreateRequest request = EventCreateRequest.from(
                "title", "content", 50, fixedDateTime, fixedDateTime.plusDays(10)
        );

        assertThatThrownBy(() -> eventCommanderService.createEvent(request))
                .isInstanceOf(BaseException.class)
                .hasMessage("이벤트 일자가 잘못되었습니다.");
    }

    @Test
    @DisplayName("이벤트 생성 실패 - 이상한 정원")
    void 이벤트_생성_실패_이상한_정원(){
        LocalDateTime fixedDateTime = LocalDateTime.of(2025, 8, 1, 12, 0);

        EventCreateRequest request = EventCreateRequest.from(
                "title", "content", 0, fixedDateTime, fixedDateTime.plusDays(10)
        );

        assertThatThrownBy(() -> eventCommanderService.createEvent(request))
                .isInstanceOf(BaseException.class)
                .hasMessage("이벤트 정원은 1명 이상이어야 합니다.");
    }

    @Test
    @DisplayName("이벤트 수정 성공")
    void 이벤트_수정_성공(){
        LocalDateTime fixedDateTime = LocalDateTime.of(2025, 8, 1, 12, 0);

        Event event = Event.from(
                "title", "content", 50, fixedDateTime, fixedDateTime.plusDays(10)
        );
        ReflectionTestUtils.setField(event, "id", 1L);

        EventCreateRequest request = EventCreateRequest.from(
                "updated title", "updated content", 100, fixedDateTime, fixedDateTime.plusDays(10)
        );

        when(eventCommander.save(any(Event.class))).thenReturn(event);
        when(eventReader.findById(1L)).thenReturn(Optional.of(event));

        EventResponse response = eventCommanderService.updateEvent(1L, request);

        assertThat(response.getTitle()).isEqualTo("updated title");
        assertThat(response.getContent()).isEqualTo("updated content");
        assertThat(response.getMaxParticipants()).isEqualTo(100);
    }

    @Test
    @DisplayName("이벤트 삭제 성공")
    void 이벤트_삭제_성공(){
        LocalDateTime fixedDateTime = LocalDateTime.of(2025, 8, 1, 12, 0);

        Event event = Event.from(
                "title", "content", 50, fixedDateTime, fixedDateTime.plusDays(10)
        );
        ReflectionTestUtils.setField(event, "id", 1L);

        when(eventReader.findById(1L)).thenReturn(Optional.of(event));

        eventCommanderService.deleteEventById(1L);

        verify(eventCommander, times(1)).delete(event);
    }

    @Test
    @DisplayName("이벤트 참가 신청 성공")
    void 이밴트_참가_신청_성공(){
        LocalDateTime fixedDateTime = LocalDateTime.of(2025, 8, 1, 12, 0);

        Event event = Event.from(
                "title", "content", 50, fixedDateTime, fixedDateTime.plusDays(10)
        );
        ReflectionTestUtils.setField(event, "id", 1L);

        when(eventReader.findById(1L)).thenReturn(Optional.of(event));
        when(eventReader.getEventParticipantByEventId(1L)).thenReturn(1L);

        eventCommanderService.addParticipant(1L, 1L);

        assertThat(event.getEventParticipants().get(0).getUserId()).isEqualTo(1L);
    }

    @Test
    void createEvent_참가인원_0명_예외() {
        LocalDateTime fixedDateTime = LocalDateTime.of(2025, 8, 1, 12, 0);

        EventCreateRequest request = EventCreateRequest.from(
                "제목", "내용", 0,fixedDateTime, fixedDateTime.plusDays(1)
        );

        assertThatThrownBy(() -> eventCommanderService.createEvent(request))
                .isInstanceOf(BaseException.class)
                .hasMessage(ErrorCode.EVENT_INVALID_PARTICIPANT.getMessage());
    }

    @Test
    @DisplayName("이벤트 참가 - 이미 참가한 유저 예외")
    void 이벤트_참가_이미_참가한_유저_예외() {
        // given
        Long eventId = 1L;
        Long userId = 10L;

        when(eventReader.existsEventByIdAndUserId(eventId, userId)).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> eventCommanderService.addParticipant(eventId, userId))
                .isInstanceOf(BaseException.class)
                .hasMessage(ErrorCode.EVENT_ALREADY_PENDING.getMessage());
    }

    @Test
    @DisplayName("이벤트 참가 - 정원 초과")
    void 이벤트_참가_정원_초과_예외() {
        LocalDateTime fixedDateTime = LocalDateTime.of(2025, 8, 1, 12, 0);

        // given
        Long eventId = 1L;
        Long userId = 10L;

        Event event = Event.from("제목", "내용", 2, fixedDateTime, fixedDateTime.plusDays(1));

        when(eventReader.existsEventByIdAndUserId(eventId, userId)).thenReturn(false);
        when(eventReader.findById(eventId)).thenReturn(Optional.of(event));
        when(eventReader.getEventParticipantByEventId(eventId)).thenReturn(2L); // 이미 정원 초과

        // when & then
        assertThatThrownBy(() -> eventCommanderService.addParticipant(eventId, userId))
                .isInstanceOf(BaseException.class)
                .hasMessage(ErrorCode.EVENT_PARTICIPANT_FULL.getMessage());
    }
}
