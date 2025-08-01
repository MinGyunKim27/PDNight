package org.example.pdnight.domain.post.presentation;

import org.example.pdnight.domain.post.application.commentUseCase.CommentService;
import org.example.pdnight.domain.post.presentation.dto.request.CommentRequestDto;
import org.example.pdnight.domain.post.presentation.dto.response.CommentResponseDto;
import org.example.pdnight.global.common.dto.ApiResponse;
import org.example.pdnight.global.common.dto.PagedResponse;
import org.example.pdnight.global.filter.CustomUserDetails;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    //댓글 다건조회 메서드
    @GetMapping("/api/posts/{postId}/comments")
    public ResponseEntity<ApiResponse<PagedResponse<CommentResponseDto>>> getComments(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        PagedResponse<CommentResponseDto> responses = commentService.getCommentsByPostId(postId, pageable);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.ok("댓글이 조회되었습니다.", responses));
    }

    //댓글 생성 메서드
    @PostMapping("/api/posts/{postId}/comments")
    public ResponseEntity<ApiResponse<CommentResponseDto>> saveComment(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails loginUser,
            @RequestBody CommentRequestDto request
    ) {
        Long loginId = loginUser.getUserId();
        CommentResponseDto response = commentService.createComment(postId, loginId, request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("댓글이 등록되었습니다.", response));
    }

    //댓글 수정 메서드
    @PatchMapping("/api/posts/{postId}/comments/{id}")
    public ResponseEntity<ApiResponse<CommentResponseDto>> updateComment(
            @PathVariable Long postId,
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails loginUser,
            @Valid @RequestBody CommentRequestDto request
    ){
        Long loginId = loginUser.getUserId();
        CommentResponseDto response = commentService.updateCommentByDto(postId, id, loginId, request);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.ok("댓글이 수정되었습니다.", response));
    }

    //대댓글 생성 메서드
    @PostMapping("/api/posts/{postId}/comments/{id}/comments")
    public ResponseEntity<ApiResponse<CommentResponseDto>> saveChildComment(
            @PathVariable Long postId,
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails loginUser,
            @Valid @RequestBody CommentRequestDto request
    ) {
        Long loginId = loginUser.getUserId();
        CommentResponseDto response = commentService.createChildComment(postId, id, loginId, request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("대댓글이 등록되었습니다.", response));
    }

    //댓글 삭제 메서드
    @DeleteMapping("/api/posts/{postId}/comments/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            @PathVariable Long postId,
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails loginUser
    ){
        Long loginId = loginUser.getUserId();
        commentService.deleteCommentById(postId, id, loginId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.ok("댓글이 삭제되었습니다.", null));
    }

    //   ------ admin ------
    @DeleteMapping("/api/admin/posts/{postId}/comments/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAdminComment(
            @PathVariable Long postId,
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails loginUser
    ) {
        Long adminId = loginUser.getUserId();
        commentService.deleteCommentByAdmin(postId, id, adminId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.ok("댓글이 삭제되었습니다.",  null));
    }

}