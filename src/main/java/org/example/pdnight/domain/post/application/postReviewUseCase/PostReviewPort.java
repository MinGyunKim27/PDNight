package org.example.pdnight.domain.post.application.postReviewUseCase;

import org.example.pdnight.domain.post.presentation.dto.response.PostParticipantInfo;
import org.springframework.stereotype.Component;

@Component
public interface PostReviewPort {
    PostParticipantInfo findById(Long postId);
}
