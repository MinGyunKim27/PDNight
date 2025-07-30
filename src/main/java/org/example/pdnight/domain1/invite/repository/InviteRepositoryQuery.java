package org.example.pdnight.domain1.invite.repository;

import org.example.pdnight.domain1.invite.dto.response.InviteResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface InviteRepositoryQuery {
    Page<InviteResponseDto> getMyInvited(Long userId, Pageable pageable);

    Page<InviteResponseDto> getMyInvite(Long userId, Pageable pageable);
}
