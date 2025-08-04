package org.example.pdnight.domain.user.presentation.dto.hobbyDto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class HobbyRequest {

    @NotNull
    String hobby;

    private HobbyRequest (String hobby) {
        this.hobby = hobby;
    }

    public static HobbyRequest from(String hobby) {
        return new HobbyRequest(hobby);
    }

}
