package org.example.pdnight.domain.promotion.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PromotionCreateRequest {
    @NotBlank(message = "제목은 필수 입력값입니다.")
    private String title;

    @NotBlank(message = "내용은 필수 입력값입니다.")
    private String content;

    @Schema(example = "2")
    @NotNull(message = "정원은 필수 입력값입니다.")
    private Integer maxParticipants;

    @NotNull(message = "프로모션 시작 날짜는 필수 입력값입니다.")
    private LocalDateTime promotionStartDate;

    @NotNull(message = "프로모션 종료 날짜는 필수 입력값입니다.")
    private LocalDateTime promotionEndDate;

    protected PromotionCreateRequest(String title, String content, Integer maxParticipants, LocalDateTime promotionStartDate, LocalDateTime promotionEndDate) {
        this.title = title;
        this.content = content;
        this.maxParticipants = maxParticipants;
        this.promotionStartDate = promotionStartDate;
        this.promotionEndDate = promotionEndDate;
    }

    public static PromotionCreateRequest from(String title, String content, Integer maxParticipants, LocalDateTime promotionStartDate, LocalDateTime promotionEndDate) {
        return new PromotionCreateRequest(title, content, maxParticipants, promotionStartDate, promotionEndDate);
    }
}
