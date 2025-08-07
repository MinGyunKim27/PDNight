package org.example.pdnight.domain.post.infra.comment;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.post.application.commentUseCase.PostPort;
import org.example.pdnight.domain.post.domain.post.Post;
import org.example.pdnight.domain.post.domain.post.PostCommander;
import org.example.pdnight.domain.post.domain.post.PostReader;
import org.example.pdnight.domain.post.presentation.dto.response.PostInfo;
import org.example.pdnight.global.common.enums.ErrorCode;
import org.example.pdnight.global.common.exception.BaseException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostAdapter implements PostPort {

    private final PostReader postReader;
    private final PostCommander postCommander;

    @Override
    public PostInfo findById(Long postId) {
        Post post = postReader.findById(postId)
                .orElseThrow(() -> new BaseException(ErrorCode.POST_NOT_FOUND));

        return PostInfo.toDto(
                post.getId(),
                post.getAuthorId(),
                post.getTitle(),
                post.getTimeSlot(),
                post.getPublicContent(),
                post.getStatus(),
                post.getMaxParticipants(),
                post.getGenderLimit(),
                post.getJobCategoryLimit(),
                post.getAgeLimit(),
                post.getIsFirstCome(),
                post.getCreatedAt(),
                post.getUpdatedAt()
        );
    }

    @Override
    public boolean existsById(Long postId) {
        return postCommander.existsById(postId);
    }

}