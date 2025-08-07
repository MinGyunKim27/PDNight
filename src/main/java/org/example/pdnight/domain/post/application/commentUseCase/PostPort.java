package org.example.pdnight.domain.post.application.commentUseCase;

import org.example.pdnight.domain.post.presentation.dto.response.PostInfo;
import org.springframework.stereotype.Component;

@Component
public interface PostPort {

    PostInfo findById(Long postId);

    boolean existsByIdAndIsDeletedIsFalse(Long postId);


}
