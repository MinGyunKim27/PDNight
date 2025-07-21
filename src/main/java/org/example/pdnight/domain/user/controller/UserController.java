package org.example.pdnight.domain.user.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.common.dto.ApiResponse;
import org.example.pdnight.domain.common.dto.PagedResponse;
import org.example.pdnight.domain.participant.enums.JoinStatus;
import org.example.pdnight.domain.post.dto.response.PostResponseDto;
import org.example.pdnight.domain.user.dto.response.PostWithJoinStatusAndAppliedAtResponseDto;
import org.example.pdnight.domain.user.service.UserService;
import org.example.pdnight.global.filter.CustomUserDetails;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.example.pdnight.domain.user.dto.request.UserPasswordUpdateRequest;
import org.example.pdnight.domain.user.dto.request.UserUpdateRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;


    @GetMapping("/my/likedPosts")
    public ResponseEntity<ApiResponse<PagedResponse<PostResponseDto>>> getMyLikedPosts(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(size = 10,page = 0) Pageable pageable
    ){
        Long id = userDetails.getUserId();
        PagedResponse<PostResponseDto> myLikedPost = userService.findMyLikedPosts(id, pageable);
        return ResponseEntity.ok(ApiResponse.ok("내 좋아요 게시글 목록이 조회되었습니다.",myLikedPost));
    }

    @GetMapping("/my/confirmedPosts")
    public ResponseEntity<ApiResponse<PagedResponse<PostWithJoinStatusAndAppliedAtResponseDto>>> getMyConfirmedPosts(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam (required = false) JoinStatus joinStatus,
            @PageableDefault(size = 10,page = 0) Pageable pageable
    ){
        Long id = userDetails.getUserId();
        PagedResponse<PostWithJoinStatusAndAppliedAtResponseDto> myLikedPost = userService.findMyConfirmedPosts(id,joinStatus, pageable);
        return ResponseEntity.ok(ApiResponse.ok("참여 신청한 게시글 목록이 조회되었습니다.",myLikedPost));
    }

    @GetMapping("/my/writtenPosts")
    public ResponseEntity<ApiResponse<PagedResponse<PostResponseDto>>> getMyWrittenPosts(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(size = 10,page = 0) Pageable pageable
    ){
        Long id = userDetails.getUserId();
        PagedResponse<PostResponseDto> myLikedPost = userService.findMyWrittenPosts(id, pageable);
        return ResponseEntity.ok(ApiResponse.ok("참여 신청한 게시글 목록이 조회되었습니다.",myLikedPost));
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<?>> getMyProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ){
        Long userId = userDetails.getUserId();
        // 로그인 중인 아이디로 프로필 조회
        return ResponseEntity.ok(ApiResponse.ok(
                "내 프로필이 조회되었습니다.",
                userService.getMyProfile(userId)
        ));
    }

    @PatchMapping("/my/profile")
    public ResponseEntity<ApiResponse<?>> updateMyProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody UserUpdateRequest requestDto
    ){
        Long userId = userDetails.getUserId();

        return ResponseEntity.ok(ApiResponse.ok(
                "프로필이 수정되었습니다.",
                userService.updateMyProfile(userId, requestDto)
        ));
    }

    @PatchMapping("/my/password")
    public ResponseEntity<ApiResponse<?>> updatePassword(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody UserPasswordUpdateRequest requestDto
    ){
        Long userId = userDetails.getUserId();
        userService.updatePassword(userId, requestDto);

        return ResponseEntity.ok(ApiResponse.ok(
                "비밀번호가 수정되었습니다.",
                null
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> getProfile(
            @PathVariable Long id
    ){
        return ResponseEntity.ok(ApiResponse.ok(
                "프로필이 조회되었습니다.",
                userService.getProfile(id, null)
        ));
    }

    @GetMapping("/{id}/evaluation")
    public ResponseEntity<ApiResponse<?>> getEvaluation(
            @PathVariable Long id
    ){
        return ResponseEntity.ok(ApiResponse.ok(
                "사용자 평가가 조회되었습니다.",
                userService.getEvaluation(id)
        ));
    }
}
