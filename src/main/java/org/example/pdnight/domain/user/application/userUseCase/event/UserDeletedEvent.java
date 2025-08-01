package org.example.pdnight.domain.user.application.userUseCase.event;

import lombok.Getter;

@Getter
public class UserDeletedEvent {
    private final Long authId;

    private UserDeletedEvent(Long authId){
        this.authId = authId;
    }

    public static UserDeletedEvent of(Long authId){
        return new UserDeletedEvent(authId);
    }
}
