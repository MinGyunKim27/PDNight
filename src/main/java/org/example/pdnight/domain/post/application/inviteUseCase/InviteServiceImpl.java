package org.example.pdnight.domain.post.application.inviteUseCase;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.post.presentation.dto.response.InviteResponseDto;
import org.example.pdnight.global.common.dto.PagedResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InviteServiceImpl implements InviteService {
    private final InviteCommanderServcie inviteCommandServcie;
    private final InviteReaderService inviteQueryService;

    @Override
    public InviteResponseDto createInvite(Long postId, Long userId, Long loginUserId) {
        return inviteCommandServcie.createInvite(postId, userId, loginUserId);
    }

    @Override
    public void deleteInvite(Long id, Long loginUserId) {
        inviteCommandServcie.deleteInvite(id, loginUserId);
    }

    @Override
    public PagedResponse<InviteResponseDto> getMyInvited(Long userId, Pageable pageable) {
        return inviteQueryService.getMyInvited(userId, pageable);
    }

    @Override
    public PagedResponse<InviteResponseDto> getMyInvite(Long userId, Pageable pageable) {
        return inviteQueryService.getMyInvite(userId, pageable);
    }
}
