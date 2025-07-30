package org.example.pdnight.domain.user.application.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.pdnight.domain.auth.presentation.dto.request.SignupRequestDto;

@Getter
public class UserRegisteredEvent {

    private final Long authId;
    private final SignupRequestDto request;

    private UserRegisteredEvent(Long authId, SignupRequestDto request) {
        this.authId = authId;
        this.request = request;
    }

    public static UserRegisteredEvent of(final Long authId, final SignupRequestDto request) {
        return new UserRegisteredEvent(authId, request);
    }

}
