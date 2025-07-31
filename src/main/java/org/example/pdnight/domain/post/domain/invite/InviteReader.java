package org.example.pdnight.domain.post.domain.invite;

import org.example.pdnight.domain.post.presentation.dto.response.InviteResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InviteReader {
    Page<InviteResponseDto> getMyInvited(Long userId, Pageable pageable);

    Page<InviteResponseDto> getMyInvite(Long userId, Pageable pageable);

}
