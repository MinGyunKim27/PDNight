package org.example.pdnight.domain.post.application.inviteUseCase;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.post.domain.post.Invite;
import org.example.pdnight.domain.post.domain.invite.InviteCommander;
import org.example.pdnight.domain.post.presentation.dto.response.InviteResponseDto;
import org.example.pdnight.global.common.exception.BaseException;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static org.example.pdnight.global.common.enums.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class InviteCommanderServcie {
    private final InviteCommander inviteCommandQuery;

    public InviteResponseDto createInvite(Long postId, Long userId, Long loginUserId) {

        validateExistInvite(postId, userId, loginUserId);
        Invite invite = Invite.create(loginUserId, userId, postId);

        inviteCommandQuery.save(invite);
        return InviteResponseDto.from(invite);
    }

    public void deleteInvite(Long inviteId, Long loginUserId) {
        Invite invite = getInviteById(inviteId);

        validateMyInvite(loginUserId, invite);

        inviteCommandQuery.delete(invite);
    }

    // -- HELPER 메서드 -- //
    // get
    private Invite getInviteById(Long id) {
        return inviteCommandQuery.findById(id)
                .orElseThrow(() -> new BaseException(INVITE_NOT_FOUND));
    }

    // validate
    private void validateExistInvite(Long postId, Long userId, Long loginUserId) {
        Boolean inviteExists = inviteCommandQuery.existsByPostIdAndInviteeIdAndInviterId(postId, userId, loginUserId);
        if (inviteExists) {
            throw new BaseException(INVITE_ALREADY_EXISTS);
        }
    }

    private void validateMyInvite(Long loginUserId, Invite invite) {
        if (!Objects.equals(invite.getInviterId(), loginUserId)) {
            throw new BaseException(INVITE_UNAUTHORIZED);
        }
    }
}
