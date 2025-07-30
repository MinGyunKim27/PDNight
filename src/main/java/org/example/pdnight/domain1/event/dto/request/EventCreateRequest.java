package org.example.pdnight.domain1.event.dto.request;

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

    @NotNull(message = "이벤트 일자는 필수 입력값입니다.")
    private LocalDateTime eventDate;
}
