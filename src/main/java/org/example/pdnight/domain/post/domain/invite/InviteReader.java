package org.example.pdnight.domain.post.domain.invite;

import org.example.pdnight.domain.post.presentation.dto.response.InviteResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface InviteReader {
    Page<InviteResponseDto> getMyInvited(Long userId, Pageable pageable);

    Page<InviteResponseDto> getMyInvite(Long userId, Pageable pageable);

    Boolean existsByPostIdAndInviteeIdAndInviterId(Long postId, Long userId, Long loginUserId);

    Optional<Object> findById(Long id);
}
