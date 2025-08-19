package org.example.pdnight.domain.post.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TagRequest {
    @Schema(description = "태그 이름", example = "Java")
    @NotNull
    private String tagName;
}
