package org.example.pdnight.domain.chat.infra;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.chat.application.chatRoomUseCase.ChatPort;
import org.example.pdnight.domain.chat.presentation.dto.response.PostInfoResponse;
import org.example.pdnight.domain.post.domain.post.Post;
import org.example.pdnight.domain.post.domain.post.PostParticipant;
import org.example.pdnight.domain.post.domain.post.PostReader;
import org.example.pdnight.domain.post.enums.JoinStatus;
import org.example.pdnight.global.common.enums.ErrorCode;
import org.example.pdnight.global.common.exception.BaseException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ChatAdaptor implements ChatPort {
    private final PostReader postReader;

    @Override
    public PostInfoResponse getPostInfoById(Long postId) {
        Post post = postReader.findById(postId).orElseThrow(
                () -> new BaseException(ErrorCode.POST_NOT_FOUND)
        );
        List<PostParticipant> postParticipants = post.getPostParticipants();
        List<PostParticipant> acceptedUser = postParticipants.stream()
                .filter(PostParticipant -> PostParticipant.getStatus().equals(JoinStatus.ACCEPTED))
                .toList();
        return PostInfoResponse.create(post.getId(), post.getAuthorId(), post.getTitle(), acceptedUser);
    }

}
