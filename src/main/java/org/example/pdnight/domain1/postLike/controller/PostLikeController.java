package org.example.pdnight.domain1.postLike.controller;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain1.common.dto.ApiResponse;
import org.example.pdnight.domain1.postLike.dto.response.PostLikeResponse;
import org.example.pdnight.domain1.postLike.service.PostLikeService;
import org.example.pdnight.global.filter.CustomUserDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts/{id}/likes")
public class PostLikeController {
    private final PostLikeService postLikeService;

    @PostMapping
    public ResponseEntity<ApiResponse<PostLikeResponse>> addLike(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        PostLikeResponse dto = postLikeService.addLike(id, userDetails.getUserId());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("게시글 좋아요가 추가되었습니다.", dto));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> removeLike(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        postLikeService.removeLike(id, userDetails.getUserId());

        return ResponseEntity.ok(ApiResponse.ok("게시글 좋아요가 삭제되었습니다.", null));
    }
}
