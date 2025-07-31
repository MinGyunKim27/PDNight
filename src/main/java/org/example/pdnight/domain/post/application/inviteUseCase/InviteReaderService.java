package org.example.pdnight.domain.post.application.inviteUseCase;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.post.domain.invite.InviteReader;
import org.example.pdnight.domain.post.presentation.dto.response.InviteResponseDto;
import org.example.pdnight.global.common.dto.PagedResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InviteReaderService {
    private final InviteReader inviteReader;

    //내가 초대 받은 목록 조회
    public PagedResponse<InviteResponseDto> getMyInvited(Long userId, Pageable pageable) {
        Page<InviteResponseDto> inviteResponseDtos = inviteReader.getMyInvited(userId, pageable);
        return PagedResponse.from(inviteResponseDtos);
    }

    //내가 초대 한 목록 조회
    public PagedResponse<InviteResponseDto> getMyInvite(Long userId, Pageable pageable) {
        Page<InviteResponseDto> inviteResponseDtos = inviteReader.getMyInvite(userId, pageable);
        return PagedResponse.from(inviteResponseDtos);
    }

}
