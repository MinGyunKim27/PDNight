package org.example.pdnight.domain.user.presentation.dto.userDto.response;

import lombok.Getter;
import org.example.pdnight.domain.user.domain.entity.Follow;

@Getter
public class FollowResponse {

    private Long following_id;
    private String following_name;

    private FollowResponse(Follow follow) {
        this.following_id = follow.getFollowing().getId();
        this.following_name = follow.getFollowing().getName();
    }

    public static FollowResponse from(Follow follow){
        return new FollowResponse(follow);
    }
}
