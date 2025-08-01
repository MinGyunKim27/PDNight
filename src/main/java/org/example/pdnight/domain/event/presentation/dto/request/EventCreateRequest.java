package org.example.pdnight.domain.event.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class EventCreateRequest {
    @NotBlank(message = "제목은 필수 입력값입니다.")
    private String title;

    @NotBlank(message = "내용은 필수 입력값입니다.")
    private String content;

    @NotNull(message = "정원은 필수 입력값입니다.")
    private Integer maxParticipants;

    @NotNull(message = "이벤트 일자는 필수 입력값입니다.")
    private LocalDateTime eventDate;

    protected EventCreateRequest(String title, String content, Integer maxParticipants, LocalDateTime eventDate) {
        this.title = title;
        this.content = content;
        this.maxParticipants = maxParticipants;
        this.eventDate = eventDate;
    }

    public static EventCreateRequest from(String title, String content, Integer maxParticipants, LocalDateTime eventDate) {
        return new EventCreateRequest(title, content, maxParticipants, eventDate);
    }
}
