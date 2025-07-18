package org.example.pdnight.domain.postLike.controller;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.common.dto.ApiResponse;
import org.example.pdnight.domain.postLike.dto.response.PostLikeResponse;
import org.example.pdnight.domain.postLike.service.PostLikeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts/{id}/likes")
public class PostLikeController {
    private final PostLikeService postLikeService;

    @PostMapping
    // todo: 추후 유저 @AuthenticationPrincipal를 통해 받아오기
    public ResponseEntity<ApiResponse<PostLikeResponse>> addLike(@PathVariable Long id) {
        Long userId = 1L;
        PostLikeResponse dto = postLikeService.addLike(id, userId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("게시글 좋아요가 추가되었습니다.", dto));
    }

    @DeleteMapping
    // todo: 추후 유저 @AuthenticationPrincipal를 통해 받아오기
    public ResponseEntity<ApiResponse<Void>> removeLike(@PathVariable Long id) {
        Long userId = 1L;

        postLikeService.removeLike(id, userId);

        return ResponseEntity.ok(ApiResponse.ok("게시글 좋아요가 삭제되었습니다.", null));
    }
}
