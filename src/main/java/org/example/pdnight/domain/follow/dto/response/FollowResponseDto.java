package org.example.pdnight.domain.follow.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.pdnight.domain.follow.entity.Follow;

@Getter
@AllArgsConstructor
public class FollowResponseDto {
    private Long id;
    private Long following_id;

    public static FollowResponseDto from(Follow follow){
        return new FollowResponseDto(follow.getId(),follow.getFollowing().getId());
    }
}
