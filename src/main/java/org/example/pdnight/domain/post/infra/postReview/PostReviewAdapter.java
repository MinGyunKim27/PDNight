package org.example.pdnight.domain.post.infra.postReview;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.post.application.postReviewUseCase.PostReviewPort;
import org.example.pdnight.domain.post.domain.post.Post;
import org.example.pdnight.domain.post.domain.post.PostParticipant;
import org.example.pdnight.domain.post.domain.post.PostReader;
import org.example.pdnight.domain.post.enums.JoinStatus;
import org.example.pdnight.domain.post.presentation.dto.response.PostParticipantInfo;
import org.example.pdnight.global.common.enums.ErrorCode;
import org.example.pdnight.global.common.exception.BaseException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PostReviewAdapter implements PostReviewPort {
    private final PostReader postReader;

    @Override
    public PostParticipantInfo findById(Long postId) {
        Post post = postReader.findById(postId).orElseThrow(() -> new BaseException(ErrorCode.POST_NOT_FOUND));
        List<Long> list = post.getPostParticipants().stream()
                .filter(participant -> participant.getStatus().equals(JoinStatus.ACCEPTED))
                .map(PostParticipant::getUserId).toList();
        return PostParticipantInfo.create(post.getAuthorId(), list);
    }
}
