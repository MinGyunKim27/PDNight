package org.example.pdnight.domain1.follow.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

@Getter
public class FollowingResponseDto {

    private Long userId;
    private String nickname;

    @QueryProjection
    public FollowingResponseDto(Long userId, String nickname) {
        this.userId = userId;
        this.nickname = nickname;
    }

}
