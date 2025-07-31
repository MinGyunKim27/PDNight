package org.example.pdnight.domain.user.application.userUseCase.event;

import lombok.Getter;

@Getter
public class UserDeleteEvent {
    private final Long authId;

    private UserDeleteEvent(Long authId){
        this.authId = authId;
    }

    public static UserDeleteEvent of(Long authId){
        return new UserDeleteEvent(authId);
    }
}
