package org.example.pdnight.domain.post.application.inviteUseCase;

import org.example.pdnight.domain.post.domain.invite.Invite;
import org.example.pdnight.domain.post.domain.invite.InviteCommandQuery;
import org.example.pdnight.domain.post.domain.invite.InviteReader;
import org.example.pdnight.domain.post.domain.post.Post;
import org.example.pdnight.domain.post.enums.JoinStatus;
import org.example.pdnight.domain.post.presentation.dto.response.InviteResponseDto;
import org.example.pdnight.domain1.common.dto.PagedResponse;
import org.example.pdnight.domain1.common.exception.BaseException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Objects;

import static org.example.pdnight.domain1.common.enums.ErrorCode.*;

public class InviteServiceImpl implements InviteService {
    private InviteReader inviteReader;
    private InviteCommandQuery inviteCommandQuery;

    @Override
    public InviteResponseDto createInvite(Long postId, Long userId, Long loginUserId) {
        Post postById = helper.getPostByIdOrElseThrow(postId);

        validateExistInvite(postId, userId, loginUserId);
        Invite invite = Invite.create(loginUserId, userId, postById);

        inviteCommandQuery.save(invite);
        return InviteResponseDto.from(invite);
    }

    @Override
    public void deleteInvite(Long inviteId, Long loginUserId) {
        Invite invite = getInviteById(inviteId);

        validateMyInvite(loginUserId, invite);

        inviteCommandQuery.delete(invite);
    }

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

    public void deleteAllByPostAndStatus(Post post, JoinStatus joinStatus) {
        inviteCommandQuery.deleteAllByPostAndStatus(post, joinStatus);
    }

    // -- HELPER 메서드 -- //
    // get
    private Invite getInviteById(Long id) {
        return inviteReader.findById(id)
                .orElseThrow(() -> new BaseException(INVITE_NOT_FOUND));
    }

    // validate
    private void validateExistInvite(Long postId, Long userId, Long loginUserId) {
        Boolean inviteExists = inviteReader.existsByPostIdAndInviteeIdAndInviterId(postId, userId, loginUserId);
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
