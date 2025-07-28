package org.example.pdnight.domain.follow.controller;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.common.dto.ApiResponse;
import org.example.pdnight.domain.follow.dto.response.FollowResponseDto;
import org.example.pdnight.domain.follow.service.FollowService;
import org.example.pdnight.global.filter.CustomUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users/{userId}/follow")
@RequiredArgsConstructor
public class FollowController {
    private final FollowService followService;

    //팔로우
    @PostMapping
    public ResponseEntity<ApiResponse<FollowResponseDto>> follow(
            @PathVariable Long userId,
            @AuthenticationPrincipal CustomUserDetails loginUser
    ){
        FollowResponseDto follow = followService.follow(userId,loginUser.getUserId());
        return ResponseEntity.ok(ApiResponse.ok("팔로우 했습니다.", follow));
    }

    //언팔로우
    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> unfollow(
            @PathVariable Long userId,
            @AuthenticationPrincipal CustomUserDetails loggedInUser
    ){
        followService.unfollow(userId,loggedInUser.getUserId());
        return ResponseEntity.ok(ApiResponse.ok("언팔로우 했습니다.",null));
    }
}
