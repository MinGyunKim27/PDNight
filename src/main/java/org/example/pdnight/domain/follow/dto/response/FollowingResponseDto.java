package org.example.pdnight.domain.follow.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import org.example.pdnight.domain.user.entity.User;

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
