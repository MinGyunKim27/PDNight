package org.example.pdnight.domain.post.application.commentUseCase;

import jakarta.validation.Valid;
import org.example.pdnight.domain.post.presentation.dto.request.CommentRequestDto;
import org.example.pdnight.domain.post.presentation.dto.response.CommentResponseDto;
import org.example.pdnight.domain1.common.dto.PagedResponse;
import org.springframework.data.domain.Pageable;

public interface CommentService {

    CommentResponseDto createComment(Long postId, Long authorId, CommentRequestDto request);

    void deleteCommentById(Long postId, Long id, Long authorId);

    CommentResponseDto updateCommentByDto(Long postId, Long id, Long authorId, @Valid CommentRequestDto request);

    CommentResponseDto createChildComment(Long postId, Long id, Long authorId, @Valid CommentRequestDto request);

    PagedResponse<CommentResponseDto> getCommentsByPostId(Long postId, Pageable pageable);

    void deleteCommentByAdmin(Long postId, Long id, Long adminId);

}