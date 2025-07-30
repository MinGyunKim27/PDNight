package org.example.pdnight.domain.post.application.inviteUseCase;

import org.example.pdnight.domain.post.presentation.dto.response.InviteResponseDto;

public interface InviteService {
    InviteResponseDto createInvite(Long postId, Long userId, Long loginUserId);

    void deleteInvite(Long id, Long loginUserId);
}
