package org.example.pdnight.domain.user.presentation.dto.hobbyDto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class HobbyRequest {

    @Schema(description = "취미", example = "러닝")
    @NotNull
    String hobby;

    private HobbyRequest (String hobby) {
        this.hobby = hobby;
    }

    public static HobbyRequest from(String hobby) {
        return new HobbyRequest(hobby);
    }

}
