package org.example.pdnight.domain.event.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    @NotNull(message = "이벤트 시작 날짜는 필수 입력값입니다.")
    private LocalDateTime eventStartDate;

    @NotNull(message = "이벤트 종료 날짜는 필수 입력값입니다.")
    private LocalDateTime eventEndDate;

    protected EventCreateRequest(String title, String content, Integer maxParticipants, LocalDateTime eventStartDate, LocalDateTime eventEndDate) {
        this.title = title;
        this.content = content;
        this.maxParticipants = maxParticipants;
        this.eventStartDate = eventStartDate;
        this.eventEndDate = eventEndDate;
    }

    public static EventCreateRequest from(String title, String content, Integer maxParticipants, LocalDateTime eventStartDate, LocalDateTime eventEndDate) {
        return new EventCreateRequest(title, content, maxParticipants, eventStartDate, eventEndDate);
    }
}
