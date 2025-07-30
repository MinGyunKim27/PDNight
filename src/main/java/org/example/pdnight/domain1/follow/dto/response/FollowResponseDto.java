package org.example.pdnight.domain1.follow.dto.response;

import lombok.Getter;
import org.example.pdnight.domain1.follow.entity.Follow;

@Getter
public class FollowResponseDto {
    private Long id;
    private Long following_id;

    private FollowResponseDto(Follow follow) {
        this.id = follow.getId();
        this.following_id = follow.getFollowing().getId();
    }

    public static FollowResponseDto from(Follow follow){
        return new FollowResponseDto(follow);
    }
}
