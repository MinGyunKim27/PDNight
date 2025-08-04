package org.example.pdnight.domain.user.presentation.dto.reviewDto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class ReviewRequest {
    @NotNull
    private BigDecimal rate;
    @Size(max = 30)
    private String comment;
}
