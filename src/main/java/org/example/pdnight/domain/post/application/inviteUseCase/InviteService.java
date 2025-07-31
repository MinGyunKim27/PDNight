package org.example.pdnight.domain.post.application.inviteUseCase;

import org.example.pdnight.domain.post.presentation.dto.response.InviteResponseDto;
import org.example.pdnight.global.common.dto.PagedResponse;
import org.springframework.data.domain.Pageable;

public interface InviteService {
    InviteResponseDto createInvite(Long postId, Long userId, Long loginUserId);

    void deleteInvite(Long id, Long loginUserId);

    PagedResponse<InviteResponseDto> getMyInvited(Long userId, Pageable pageable);

    PagedResponse<InviteResponseDto> getMyInvite(Long userId, Pageable pageable);
}
