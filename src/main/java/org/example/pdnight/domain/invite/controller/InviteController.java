package org.example.pdnight.domain.invite.controller;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.common.dto.ApiResponse;
import org.example.pdnight.domain.invite.dto.response.InviteResponseDto;
import org.example.pdnight.domain.invite.service.InviteService;
import org.example.pdnight.global.filter.CustomUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.parameters.P;
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
        return ResponseEntity.created(location).body(ApiResponse.ok("초대가 완료되었습니다.",responseDto));
    }

    @DeleteMapping("/post/{postId}/users/{userId}/invite/{id}")
    public ResponseEntity<?> deleteInvite(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails loginUser
    ){
        Long loginUserId = loginUser.getUserId();

        inviteService.deleteInvite(id,loginUserId);
        return ResponseEntity.noContent().build();
    }
}
