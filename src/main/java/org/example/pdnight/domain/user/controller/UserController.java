package org.example.pdnight.domain.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.common.dto.ApiResponse;
import org.example.pdnight.domain.common.dto.PagedResponse;
import org.example.pdnight.domain.common.enums.JoinStatus;
import org.example.pdnight.domain.invite.dto.response.InviteResponseDto;
import org.example.pdnight.domain.invite.service.InviteService;
import org.example.pdnight.domain.post.dto.response.PostResponseWithApplyStatusDto;
import org.example.pdnight.domain.post.service.PostService;
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
    private final PostService postService;
    private final InviteService inviteService;


    // 내 좋아요 게시글 목록 조회
    @GetMapping("/my/likedPosts")
    public ResponseEntity<ApiResponse<?>> getMyLikedPosts(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(size = 10,page = 0) Pageable pageable
    ){
        Long id = userDetails.getUserId();
        PagedResponse<PostResponseWithApplyStatusDto> myLikedPost = postService.findMyLikedPosts(id, pageable);
        return ResponseEntity.ok(ApiResponse.ok("내 좋아요 게시글 목록이 조회되었습니다.",myLikedPost));
    }

    //내 신청/성사된 게시글 조회
    @GetMapping("/my/confirmedPosts")
    public ResponseEntity<ApiResponse<?>> getMyConfirmedPosts(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam (required = false) JoinStatus joinStatus,
            @PageableDefault(size = 10,page = 0) Pageable pageable
    ){
        Long id = userDetails.getUserId();
        PagedResponse<PostWithJoinStatusAndAppliedAtResponseDto> myLikedPost = postService.findMyConfirmedPosts(id,joinStatus, pageable);
        return ResponseEntity.ok(ApiResponse.ok("참여 신청한 게시글 목록이 조회되었습니다.",myLikedPost));
    }


    // 내가 작성한 게시글 조회
    @GetMapping("/my/writtenPosts")
    public ResponseEntity<ApiResponse<?>> getMyWrittenPosts(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(size = 10,page = 0) Pageable pageable
    ){
        Long id = userDetails.getUserId();
        PagedResponse<PostResponseWithApplyStatusDto> myLikedPost = postService.findMyWrittenPosts(id, pageable);
        return ResponseEntity.ok(ApiResponse.ok("내가 작성 한 게시물이 조회되었습니다.",myLikedPost));
    }


    // 본인 프로필 조회
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

    // 본인 프로필 수정
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

    // 비밀번호 변경
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


    // 유저 프로필 조회
    @GetMapping("/{id}/profile")
    public ResponseEntity<ApiResponse<?>> getProfile(
            @PathVariable Long id
    ){
        return ResponseEntity.ok(ApiResponse.ok(
                "프로필이 조회되었습니다.",
                userService.getProfile(id)
        ));
    }


    //평가 조회
    @GetMapping("/{id}/evaluation")
    public ResponseEntity<ApiResponse<?>> getEvaluation(
            @PathVariable Long id
    ){
        return ResponseEntity.ok(ApiResponse.ok(
                "사용자 평가가 조회되었습니다.",
                userService.getEvaluation(id)
        ));
    }

    // -------------------- 내 초대 API -----------------------------------------//
    //내 초대받은 목록 조회
    @GetMapping("/my/invited")
    public ResponseEntity<ApiResponse<?>> getMyInvited(
            @AuthenticationPrincipal CustomUserDetails loggedInUser
    ){
        Long userId = loggedInUser.getUserId();
        PagedResponse<InviteResponseDto> inviteResponseDto = inviteService.getMyInvited(userId);

        return ResponseEntity.ok(ApiResponse.ok("초대 받은 목록 조회가 완료되었습니다",inviteResponseDto));
    }

    //내가 보낸 초대 목록 조회

}
