package org.example.pdnight.domain.user.dto.response;

import lombok.Getter;
import org.example.pdnight.domain.user.entity.User;
import java.time.LocalDateTime;

@Getter
public class UserEvalueationResponse {
    private Long id;
    private float rate;
    private LocalDateTime createdAt;

    public UserEvalueationResponse(User user) {
        this.id = user.getId();
        this.rate = user.getTotalRate().floatValue();
        this.createdAt = user.getCreatedAt();
    }
}
