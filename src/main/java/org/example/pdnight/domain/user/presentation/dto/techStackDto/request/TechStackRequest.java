package org.example.pdnight.domain.user.presentation.dto.techStackDto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TechStackRequest {
    @NotNull
    private String techStack;
}
