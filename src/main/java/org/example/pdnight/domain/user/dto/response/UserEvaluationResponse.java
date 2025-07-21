package org.example.pdnight.domain.user.dto.response;

import lombok.Getter;
import org.example.pdnight.domain.user.entity.User;
import java.time.LocalDateTime;

@Getter
public class UserEvaluationResponse {
    private Long id;
    private float rate;
    private LocalDateTime createdAt;

    public UserEvaluationResponse(User user) {
        this.id = user.getId();
        this.rate = (float) user.getTotalRate() / user.getTotalReviewer();
        this.createdAt = user.getCreatedAt();
    }
}
