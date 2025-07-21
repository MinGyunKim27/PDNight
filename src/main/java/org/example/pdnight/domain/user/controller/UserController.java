package org.example.pdnight.domain.user.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
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
import org.example.pdnight.domain.user.dto.request.UserPasswordUpdateRequest;
import org.example.pdnight.domain.user.dto.request.UserUpdateRequest;
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
            @RequestParam (required = false) JoinStatus joinStatus,
            @PageableDefault(size = 10,page = 0) Pageable pageable
    ){
        PagedResponse<PostWithJoinStatusAndAppliedAtResponseDto> myLikedPost = userService.findMyConfirmedPosts(id,joinStatus, pageable);
        return ResponseEntity.ok(ApiResponse.ok("참여 신청한 게시글 목록이 조회되었습니다.",myLikedPost));
    }
}

    @GetMapping("/my")
    public ApiResponse<?> getMyProfile(
            HttpServletRequest request
    ){
        // todo : @AuthenticationPrincipal를 통해 id 가져오기
        // 로그인 중인 아이디로 프로필 조회
        return ApiResponse.ok(
                "내 프로필이 조회되었습니다.",
                userService.getMyProfile(1L)
        );
    }

    @PatchMapping("/my/profile")
    public ApiResponse<?> updateMyProfile(
            HttpServletRequest request,
            @RequestBody UserUpdateRequest requestDto
    ){
        // todo : @AuthenticationPrincipal를 통해 id 가져오기
        return ApiResponse.ok(
                "프로필이 수정되었습니다.",
                userService.updateMyProfile(1L, requestDto)
        );
    }

    @PatchMapping("/my/password")
    public ApiResponse<?> updatePassword(
            HttpServletRequest request,
            @Valid @RequestBody UserPasswordUpdateRequest requestDto
    ){
        // todo : @AuthenticationPrincipal를 통해 id 가져오기
        userService.updatePassword(1L, requestDto);

        return ApiResponse.ok(
                "비밀번호가 수정되었습니다.",
                null
        );
    }

    @GetMapping("/{id}")
    public ApiResponse<?> getProfile(
            @PathVariable Long id,
            HttpServletRequest request
    ){
        // todo : @AuthenticationPrincipal를 통해 id 가져오기
        return ApiResponse.ok(
                "프로필이 조회되었습니다.",
                userService.getProfile(id, null)
        );
    }
}

