package org.example.pdnight.domain.user.presentation.dto.userDto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

@Getter
public class FollowingResponse {

    private Long userId;
    private String nickname;

    @QueryProjection
    public FollowingResponse(Long userId, String nickname) {
        this.userId = userId;
        this.nickname = nickname;
    }

}
