package org.example.pdnight.domain.post.infra.invite;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.post.domain.invite.Invite;
import org.example.pdnight.domain.post.domain.invite.InviteCommander;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class InviteCommanderImpl implements InviteCommander {

    private final InviteJpaRepository inviteJpaRepository;

    @Override
    public void save(Invite invite) {
        inviteJpaRepository.save(invite);
    }

    @Override
    public void delete(Invite invite) {
        inviteJpaRepository.delete(invite);
    }

    @Override
    public Optional<Invite> findById(Long id) {
        return inviteJpaRepository.findById(id);
    }

    @Override
    public Boolean existsByPostIdAndInviteeIdAndInviterId(Long postId, Long userId, Long loginUserId) {
        return inviteJpaRepository.existsByPostIdAndInviteeIdAndInviterId(postId, userId, loginUserId);
    }

}