package org.example.pdnight.domain.hobby.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class HobbyRequest {
    @NotNull
    String hobby;
}
