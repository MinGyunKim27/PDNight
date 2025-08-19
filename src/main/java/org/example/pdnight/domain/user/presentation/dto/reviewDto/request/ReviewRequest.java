package org.example.pdnight.domain.user.presentation.dto.reviewDto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class ReviewRequest {
    @Schema(description = "평가 점수", example = "3")
    @NotNull
    private BigDecimal rate;

    @Schema(description = "리뷰 내용", example = "comment")
    @Size(max = 30)
    private String comment;
}
