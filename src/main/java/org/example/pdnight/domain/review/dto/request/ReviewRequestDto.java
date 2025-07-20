package org.example.pdnight.domain.review.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class ReviewRequestDto {
    @NotNull
    private BigDecimal rate;
    @Size(max = 30)
    private String comment;
}
