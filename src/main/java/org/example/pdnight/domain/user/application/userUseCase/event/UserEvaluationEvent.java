package org.example.pdnight.domain.user.application.userUseCase.event;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class UserEvaluationEvent {

    private final Long userId;
    private final BigDecimal rate;

    private UserEvaluationEvent(Long userId, BigDecimal rate) {
        this.userId = userId;
        this.rate = rate;
    }

    public static UserEvaluationEvent of(Long userId, BigDecimal rate) {
        return new UserEvaluationEvent(userId, rate);
    }
}
