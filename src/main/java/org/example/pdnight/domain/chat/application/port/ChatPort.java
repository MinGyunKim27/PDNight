package org.example.pdnight.domain.chat.application.port;

import org.example.pdnight.domain.chat.presentation.dto.response.PostInfoResponse;

public interface ChatPort {
    PostInfoResponse getPostInfoById(Long postId);
}
