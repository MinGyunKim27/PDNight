package org.example.pdnight.domain1.comment.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain1.comment.dto.request.CommentRequestDto;
import org.example.pdnight.domain1.comment.dto.response.CommentResponseDto;
import org.example.pdnight.domain1.comment.service.CommentService;
import org.example.pdnight.domain1.common.dto.ApiResponse;
import org.example.pdnight.domain1.common.dto.PagedResponse;
import org.example.pdnight.global.filter.CustomUserDetails;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts/{postId}/comments")
@RequiredArgsConstructor
public class CommentController {

	private final CommentService commentService;

	//댓글 생성 메서드
	@PostMapping
	public ResponseEntity<ApiResponse<CommentResponseDto>> saveComment(
		@PathVariable Long postId,
		@AuthenticationPrincipal CustomUserDetails loginUser,
		@RequestBody CommentRequestDto request
	) {
		Long authorId = loginUser.getUserId();
		CommentResponseDto response = commentService.createComment(postId, authorId, request);

		return ResponseEntity.status(HttpStatus.CREATED)
			.body(ApiResponse.ok("댓글이 등록되었습니다.", response));
	}

	//댓글 삭제 메서드
	@DeleteMapping("/{id}")
	public ResponseEntity<ApiResponse<Void>> deleteComment(
		@PathVariable Long postId,
		@PathVariable Long id,
		@AuthenticationPrincipal CustomUserDetails loginUser
	){
		Long authorId = loginUser.getUserId();
		commentService.deleteCommentById(postId, id, authorId);

		return ResponseEntity.status(HttpStatus.OK)
			.body(ApiResponse.ok("댓글이 삭제되었습니다.", null));
	}

	//댓글 수정 메서드
	@PatchMapping("/{id}")
	public ResponseEntity<ApiResponse<CommentResponseDto>> updateComment(
		@PathVariable Long postId,
		@PathVariable Long id,
		@AuthenticationPrincipal CustomUserDetails loginUser,
		@Valid @RequestBody CommentRequestDto request
	){
		Long authorId = loginUser.getUserId();
		CommentResponseDto response = commentService.updateCommentByDto(postId, id, authorId, request);

		return ResponseEntity.status(HttpStatus.OK)
			.body(ApiResponse.ok("댓글이 수정되었습니다.", response));
	}

	//대댓글 생성 메서드
	@PostMapping("/{id}/comments")
	public ResponseEntity<ApiResponse<CommentResponseDto>> saveChildComment(
		@PathVariable Long postId,
		@PathVariable Long id,
		@AuthenticationPrincipal CustomUserDetails loginUser,
		@Valid @RequestBody CommentRequestDto request
	) {
		Long authorId = loginUser.getUserId();
		CommentResponseDto response = commentService.createChildComment(postId, id, authorId, request);

		return ResponseEntity.status(HttpStatus.CREATED)
			.body(ApiResponse.ok("대댓글이 등록되었습니다.", response));
	}

	//댓글 다건조회 메서드
	@GetMapping
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

}