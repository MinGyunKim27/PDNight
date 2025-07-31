package org.example.pdnight.domain.post.application.commentUseCase;

import org.example.pdnight.domain.post.presentation.dto.request.CommentRequestDto;
import org.example.pdnight.domain.post.presentation.dto.response.CommentResponseDto;
import org.example.pdnight.global.common.dto.PagedResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {

	private final CommentReaderService commentQueryService;
	private final CommentCommanderService commentCommandService;

	@Override
	public CommentResponseDto createComment(Long postId, Long loginId, CommentRequestDto request) {
		return commentCommandService.createComment(postId, loginId, request);
	}

	@Override
	public void deleteCommentById(Long postId, Long id, Long loginId) {
		commentCommandService.deleteCommentById(postId, id, loginId);
	}

	@Override
	public CommentResponseDto updateCommentByDto(Long postId, Long id, Long loginId, CommentRequestDto request) {
		return commentCommandService.updateCommentByDto(postId, id, loginId, request);
	}

	@Override
	public CommentResponseDto createChildComment(Long postId, Long id, Long loginId, CommentRequestDto request) {
		return commentCommandService.createChildComment(postId, id, loginId, request);
	}

	@Override
	public void deleteCommentByAdmin(Long postId, Long id, Long adminId) {
		commentCommandService.deleteCommentByAdmin(postId, id, adminId);
	}

	@Override
	public PagedResponse<CommentResponseDto> getCommentsByPostId(Long postId, Pageable pageable) {
		return commentQueryService.getCommentsByPostId(postId, pageable);
	}

}