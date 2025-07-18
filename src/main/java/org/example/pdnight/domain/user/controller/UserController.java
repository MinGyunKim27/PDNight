package org.example.pdnight.domain.user.controller;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.common.dto.ApiResponse;
import org.example.pdnight.domain.common.dto.PagedResponse;
import org.example.pdnight.domain.participant.enums.JoinStatus;
import org.example.pdnight.domain.post.dto.response.PostResponseDto;
import org.example.pdnight.domain.user.dto.request.ConfirmedPostRequestDto;
import org.example.pdnight.domain.user.dto.response.PostWithJoinStatusAndAppliedAtResponseDto;
import org.example.pdnight.domain.user.service.UserService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/my/likedPosts")
    public ResponseEntity<ApiResponse<PagedResponse<PostResponseDto>>> getMyLikedPosts(
            @RequestParam Long id,
            @PageableDefault(size = 10,page = 0) Pageable pageable
    ){
        PagedResponse<PostResponseDto> myLikedPost = userService.findMyLikedPosts(id, pageable);
        return ResponseEntity.ok(ApiResponse.ok("내 좋아요 게시글 목록이 조회되었습니다.",myLikedPost));
    }

    @GetMapping("/my/confirmedPosts")
    public ResponseEntity<ApiResponse<PagedResponse<PostWithJoinStatusAndAppliedAtResponseDto>>> getMyConfirmedPosts(
            @RequestParam Long id,
            @RequestParam JoinStatus joinStatus,
            @PageableDefault(size = 10,page = 0) Pageable pageable
    ){
        PagedResponse<PostWithJoinStatusAndAppliedAtResponseDto> myLikedPost = userService.findMyConfirmedPosts(id,joinStatus, pageable);
        return ResponseEntity.ok(ApiResponse.ok("참여 신청한 게시글 목록이 조회되었습니다.",myLikedPost));
    }
}
