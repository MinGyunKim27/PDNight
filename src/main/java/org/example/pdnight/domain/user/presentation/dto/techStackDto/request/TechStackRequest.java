package org.example.pdnight.domain.user.presentation.dto.techStackDto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TechStackRequest {
    @Schema(description = "기술 스택", example = "JPA")
    @NotNull
    private String techStack;
}
