package org.example.pdnight.domain1.user.dto.response;

import lombok.Getter;
import org.example.pdnight.domain1.user.entity.User;

import java.time.LocalDateTime;

@Getter
public class UserEvaluationResponse {
    private Long id;
    private float rate;
    private LocalDateTime createdAt;

    private UserEvaluationResponse(User user) {
        this.id = user.getId();
        this.rate = (float) user.getTotalRate() / user.getTotalReviewer();
        this.createdAt = user.getCreatedAt();
    }

    public static UserEvaluationResponse from(User user) {
        return new UserEvaluationResponse(user);
    }
}
