package org.example.pdnight.domain.hobby.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class HobbyRequest {
    @NotNull
    String hobby;
}
