package org.example.pdnight.domain.comment.controller;

import org.example.pdnight.domain.comment.service.AdminCommentService;
import org.example.pdnight.domain.common.dto.ApiResponse;
import org.example.pdnight.global.filter.CustomUserDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/posts/{postId}/comments")
@RequiredArgsConstructor
public class AdminCommentController {
	private final AdminCommentService adminCommentService;

	@DeleteMapping("/{id}")
	public ResponseEntity<ApiResponse<Void>> deleteComment(
		@PathVariable Long postId,
		@PathVariable Long id,
		@AuthenticationPrincipal CustomUserDetails loginUser
	) {
		Long adminId = loginUser.getUserId();
		adminCommentService.deleteCommentByAdmin(postId, id, adminId);

		return ResponseEntity.status(HttpStatus.OK)
			.body(ApiResponse.ok("댓글이 삭제되었습니다.",  null));
	}

}