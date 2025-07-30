package org.example.pdnight.domain.post.presentation;

import lombok.RequiredArgsConstructor;

import org.example.pdnight.domain.post.application.inviteUseCase.InviteService;
import org.example.pdnight.domain.post.presentation.dto.response.InviteResponseDto;
import org.example.pdnight.domain1.common.dto.ApiResponse;
import org.example.pdnight.global.filter.CustomUserDetails;
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

}

