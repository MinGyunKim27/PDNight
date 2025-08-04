package org.example.pdnight.domain.user.application.userUseCase.event;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class UserEvaluatedEvent {

    private final Long userId;
    private final BigDecimal rate;

    private UserEvaluatedEvent(Long userId, BigDecimal rate) {
        this.userId = userId;
        this.rate = rate;
    }

    public static UserEvaluatedEvent of(Long userId, BigDecimal rate) {
        return new UserEvaluatedEvent(userId, rate);
    }
}
