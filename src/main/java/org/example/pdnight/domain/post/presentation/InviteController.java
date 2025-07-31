package org.example.pdnight.domain.post.presentation;

import lombok.RequiredArgsConstructor;

import org.example.pdnight.domain.post.application.inviteUseCase.InviteService;
import org.example.pdnight.domain.post.presentation.dto.response.InviteResponseDto;
import org.example.pdnight.global.common.dto.ApiResponse;
import org.example.pdnight.global.common.dto.PagedResponse;
import org.example.pdnight.global.filter.CustomUserDetails;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class InviteController {
    private final InviteService inviteService;

    @PostMapping("/post/{postId}/users/{userId}/invite")
    public ResponseEntity<ApiResponse<InviteResponseDto>> inviteUser(
            @PathVariable Long postId,
            @PathVariable Long userId,
            @AuthenticationPrincipal CustomUserDetails loginUser
    ){
        Long loginUserId = loginUser.getUserId();
        InviteResponseDto responseDto = inviteService.createInvite(postId,userId,loginUserId);
        URI location = URI.create("/api/posts/" + postId);
        return ResponseEntity.created(location).body(ApiResponse.ok("초대가 완료되었습니다.", responseDto));
    }

    @DeleteMapping("/post/{postId}/users/{userId}/invite/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteInvite(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails loginUser
    ) {
        Long loginUserId = loginUser.getUserId();

        inviteService.deleteInvite(id, loginUserId);
        return ResponseEntity.ok(ApiResponse.ok("초대가 삭제되었습니다.", null));
    }

    // -------------------- 내 초대 API -----------------------------------------//
    //내 초대받은 목록 조회
    @GetMapping("/api/users/my/invited")
    public ResponseEntity<ApiResponse<PagedResponse<InviteResponseDto>>> getMyInvited(
            @AuthenticationPrincipal CustomUserDetails loggedInUser,
            @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        Long userId = loggedInUser.getUserId();
        PagedResponse<InviteResponseDto> inviteResponseDto = inviteService.getMyInvited(userId, pageable);

        return ResponseEntity.ok(ApiResponse.ok("초대 받은 목록 조회가 완료되었습니다", inviteResponseDto));
    }

    //내가 보낸 초대 목록 조회
    @GetMapping("/api/users/my/invite")
    public ResponseEntity<ApiResponse<PagedResponse<InviteResponseDto>>> getMyInvite(
            @AuthenticationPrincipal CustomUserDetails loggedInUser,
            @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        Long userId = loggedInUser.getUserId();
        PagedResponse<InviteResponseDto> inviteResponseDto = inviteService.getMyInvite(userId, pageable);

        return ResponseEntity.ok(ApiResponse.ok("초대 받은 목록 조회가 완료되었습니다", inviteResponseDto));
    }

}

