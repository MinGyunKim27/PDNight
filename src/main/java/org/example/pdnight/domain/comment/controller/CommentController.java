package org.example.pdnight.domain.comment.controller;

import org.example.pdnight.domain.comment.dto.request.CommentRequestDto;
import org.example.pdnight.domain.comment.dto.response.CommentResponseDto;
import org.example.pdnight.domain.comment.service.CommentService;
import org.example.pdnight.domain.common.dto.ApiResponse;
import org.example.pdnight.global.filter.CustomUserDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

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

}