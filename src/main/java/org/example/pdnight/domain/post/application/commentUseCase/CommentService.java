package org.example.pdnight.domain.post.application.commentUseCase;

import jakarta.validation.Valid;
import org.example.pdnight.domain.post.presentation.dto.request.CommentRequest;
import org.example.pdnight.domain.post.presentation.dto.response.CommentResponse;
import org.example.pdnight.global.common.dto.PagedResponse;
import org.springframework.data.domain.Pageable;

public interface CommentService {

    CommentResponse createComment(Long postId, Long authorId, CommentRequest request);

    void deleteCommentById(Long postId, Long id, Long authorId);

    CommentResponse updateCommentByDto(Long postId, Long id, Long authorId, @Valid CommentRequest request);

    CommentResponse createChildComment(Long postId, Long id, Long authorId, @Valid CommentRequest request);

    PagedResponse<CommentResponse> getCommentsByPostId(Long postId, Pageable pageable);

    void deleteCommentByAdmin(Long postId, Long id, Long adminId);

}