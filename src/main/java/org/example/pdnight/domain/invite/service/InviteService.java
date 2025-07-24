package org.example.pdnight.domain.invite.service;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.common.dto.PagedResponse;
import org.example.pdnight.domain.common.enums.JoinStatus;
import org.example.pdnight.domain.common.exception.BaseException;
import org.example.pdnight.domain.invite.dto.response.InviteResponseDto;
import org.example.pdnight.domain.invite.entity.Invite;
import org.example.pdnight.domain.invite.repository.InviteRepository;
import org.example.pdnight.domain.invite.repository.InviteRepositoryQuery;
import org.example.pdnight.domain.post.entity.Post;
import org.example.pdnight.domain.post.service.PostService;
import org.example.pdnight.domain.user.entity.User;
import org.example.pdnight.domain.user.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static org.example.pdnight.domain.common.enums.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class InviteService {
    private final InviteRepository inviteRepository;
    private final InviteRepositoryQuery inviteRepositoryQuery;
    private final PostService postService;
    private final UserService userService;

    public InviteResponseDto createInvite(Long postId, Long userId,Long loginUserId) {
        Post postById = postService.getPostById(postId);
        User inviteeById = userService.getUserById(userId);
        User me = userService.getUserById(loginUserId);

        Boolean inviteExists = inviteRepository.existsByPostIdAndInviteeIdAndInviterId(postId,userId,loginUserId);

        if (inviteExists){
            throw new BaseException(INVITE_ALREADY_EXISTS);
        }
        Invite invite = Invite.create(me, inviteeById,postById);

        inviteRepository.save(invite);
        return new InviteResponseDto(invite);
    }

    public void deleteInvite(Long inviteId, Long loginUserId) {
        Invite invite = inviteRepository.findById(inviteId).orElseThrow(
                () ->new BaseException(INVITE_NOT_FOUND));

        if (!Objects.equals(invite.getInviter().getId(), loginUserId)) {
            throw new BaseException(INVITE_UNAUTHORIZED);
        }

        inviteRepository.delete(invite);
    }

    public PagedResponse<InviteResponseDto> getMyInvited(Long userId) {
        Page<InviteResponseDto> inviteResponseDtos = inviteRepositoryQuery.getMyInvited(userId);
        return null;
    }

    public void deleteAllByPostAndStatus(Post post,JoinStatus joinStatus){
        inviteRepository.deleteAllByPostAndStatus(post, joinStatus);
    };
}
