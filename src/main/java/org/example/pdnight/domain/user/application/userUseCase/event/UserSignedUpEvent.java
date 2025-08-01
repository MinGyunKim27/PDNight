package org.example.pdnight.domain.user.application.userUseCase.event;

import lombok.Getter;
import org.example.pdnight.domain.auth.presentation.dto.request.SignupRequest;

@Getter
public class UserSignedUpEvent {

    private final Long authId;
    private final SignupRequest request;

    private UserSignedUpEvent(Long authId, SignupRequest request) {
        this.authId = authId;
        this.request = request;
    }

    public static UserSignedUpEvent of(final Long authId, final SignupRequest request) {
        return new UserSignedUpEvent(authId, request);
    }

}
