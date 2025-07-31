package org.example.pdnight.domain.user.application.userUseCase.event;

import lombok.Getter;
import org.example.pdnight.domain.auth.presentation.dto.request.SignupRequest;

@Getter
public class UserSignUpEvent {

    private final Long authId;
    private final SignupRequest request;

    private UserSignUpEvent(Long authId, SignupRequest request) {
        this.authId = authId;
        this.request = request;
    }

    public static UserSignUpEvent of(final Long authId, final SignupRequest request) {
        return new UserSignUpEvent(authId, request);
    }

}
