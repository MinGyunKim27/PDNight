package org.example.pdnight.domain.post.application.commentUseCase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.pdnight.domain.post.presentation.dto.request.CommentRequest;
import org.example.pdnight.domain.post.presentation.dto.response.CommentResponse;
import org.example.pdnight.global.aop.SaveLog;
import org.example.pdnight.global.common.dto.PagedResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {

    private final CommentReaderService commentQueryService;
    private final CommentCommanderService commentCommandService;

    @Override
    public CommentResponse createComment(Long postId, Long loginId, CommentRequest request) {
        return commentCommandService.createComment(postId, loginId, request);
    }

    @Override
    public void deleteCommentById(Long postId, Long id, Long loginId) {
        commentCommandService.deleteCommentById(postId, id, loginId);
    }

    @Override
    public CommentResponse updateCommentByDto(Long postId, Long id, Long loginId, CommentRequest request) {
        return commentCommandService.updateCommentByDto(postId, id, loginId, request);
    }

    @Override
    public CommentResponse createChildComment(Long postId, Long id, Long loginId, CommentRequest request) {
        return commentCommandService.createChildComment(postId, id, loginId, request);
    }

    @SaveLog
    @Override
    public void deleteCommentByAdmin(Long postId, Long id, Long adminId) {
        commentCommandService.deleteCommentByAdmin(postId, id, adminId);
    }

    @Override
    public PagedResponse<CommentResponse> getCommentsByPostId(Long postId, Pageable pageable) {
        return commentQueryService.getCommentsByPostId(postId, pageable);
    }

}