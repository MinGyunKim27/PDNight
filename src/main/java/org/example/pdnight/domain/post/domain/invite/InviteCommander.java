package org.example.pdnight.domain.post.domain.invite;

import java.util.Optional;

public interface InviteCommander {
    void save(Invite invite);

    void delete(Invite invite);

    Optional<Invite> findById(Long id);

    Boolean existsByPostIdAndInviteeIdAndInviterId(Long postId, Long userId, Long loginUserId);
}
