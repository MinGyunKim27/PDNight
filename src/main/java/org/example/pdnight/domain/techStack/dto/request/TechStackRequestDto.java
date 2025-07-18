package org.example.pdnight.domain.techStack.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class TechStackRequestDto {
    @NotNull
    private String techStack;
}
