package org.example.pdnight.domain1.techStack.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TechStackRequestDto {
    @NotNull
    private String techStack;
}
