package org.example.pdnight.domain.invite.repository;

import org.example.pdnight.domain.invite.dto.response.InviteResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;

@Repository
public interface InviteRepositoryQuery {
    Page<InviteResponseDto> getMyInvited(Long userId);
}
