package org.example.pdnight.domain.post.application.commentUseCase;

import org.example.pdnight.domain.post.presentation.dto.response.PostInfo;

public interface PostPort {

    PostInfo findById(Long postId);

    boolean existsById(Long postId);

}