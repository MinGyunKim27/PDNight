package org.example.pdnight.domain.post.infra.comment;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.post.application.PostUseCase.PostInfoAssembler;
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
    private final PostInfoAssembler postInfoAssembler;

    @Override
    public PostInfo findById(Long postId) {
        Post post = postReader.findById(postId)
                .orElseThrow(() -> new BaseException(ErrorCode.POST_NOT_FOUND));

        return postInfoAssembler.toInfoDto(post);
    }

    @Override
    public boolean existsByIdAndIsDeletedIsFalse(Long postId) {
        return postCommander.existsByIdAndIsDeletedIsFalse(postId);
    }

}
