package org.example.pdnight.domain.post.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.post.application.commentUseCase.CommentService;
import org.example.pdnight.domain.post.presentation.dto.request.CommentRequest;
import org.example.pdnight.domain.post.presentation.dto.response.CommentResponse;
import org.example.pdnight.global.common.dto.ApiResponse;
import org.example.pdnight.global.common.dto.PagedResponse;
import org.example.pdnight.global.filter.CustomUserDetails;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Comment")
@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    //댓글 다건조회 메서드
    @GetMapping("/api/posts/{postId}/comments")
    @Operation(summary = "댓글 리스트 조회", description = "특정 게시글의 댓글 전체를 조회한다")
    public ResponseEntity<ApiResponse<PagedResponse<CommentResponse>>> getComments(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        PagedResponse<CommentResponse> responses = commentService.getCommentsByPostId(postId, pageable);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.ok("댓글이 조회되었습니다.", responses));
    }

    //댓글 생성 메서드
    @PostMapping("/api/posts/{postId}/comments")
    @Operation(summary = "댓글 등록", description = "특정 게시글에 댓글을 등록한다")
    public ResponseEntity<ApiResponse<CommentResponse>> saveComment(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails loginUser,
            @RequestBody CommentRequest request
    ) {
        Long loginId = loginUser.getUserId();
        CommentResponse response = commentService.createComment(postId, loginId, request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("댓글이 등록되었습니다.", response));
    }

    //댓글 수정 메서드
    @PatchMapping("/api/posts/{postId}/comments/{id}")
    @Operation(summary = "댓글 수정", description = "댓글을 수정한다")
    public ResponseEntity<ApiResponse<CommentResponse>> updateComment(
            @PathVariable Long postId,
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails loginUser,
            @Valid @RequestBody CommentRequest request
    ) {
        Long loginId = loginUser.getUserId();
        CommentResponse response = commentService.updateCommentByDto(postId, id, loginId, request);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.ok("댓글이 수정되었습니다.", response));
    }

    //대댓글 생성 메서드
    @PostMapping("/api/posts/{postId}/comments/{id}/comments")
    @Operation(summary = "대댓글 등록", description = "대댓글을 등록한다")
    public ResponseEntity<ApiResponse<CommentResponse>> saveChildComment(
            @PathVariable Long postId,
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails loginUser,
            @Valid @RequestBody CommentRequest request
    ) {
        Long loginId = loginUser.getUserId();
        CommentResponse response = commentService.createChildComment(postId, id, loginId, request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("대댓글이 등록되었습니다.", response));
    }

    //댓글 삭제 메서드
    @DeleteMapping("/api/posts/{postId}/comments/{id}")
    @Operation(summary = "댓글 삭제", description = "댓글을 삭제한다")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            @PathVariable Long postId,
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails loginUser
    ) {
        Long loginId = loginUser.getUserId();
        commentService.deleteCommentById(postId, id, loginId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.ok("댓글이 삭제되었습니다.", null));
    }

    //   ------ admin ------
    @DeleteMapping("/api/admin/posts/{postId}/comments/{id}")
    @Operation(summary = "[관리자] 댓글 강제 삭제", description = "관리자가 댓글을 강제로 삭제한다")
    public ResponseEntity<ApiResponse<Void>> deleteAdminComment(
            @PathVariable Long postId,
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails loginUser
    ) {
        Long adminId = loginUser.getUserId();
        commentService.deleteCommentByAdmin(postId, id, adminId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.ok("댓글이 삭제되었습니다.", null));
    }

}
