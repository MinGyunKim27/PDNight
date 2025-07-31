package org.example.pdnight.domain.post.infra.invite;

import org.example.pdnight.domain.post.domain.invite.Invite;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InviteJpaRepository extends JpaRepository<Invite, Long> {

    Boolean existsByPostIdAndInviteeIdAndInviterId(Long postId, Long userId, Long loginUserId);
}
