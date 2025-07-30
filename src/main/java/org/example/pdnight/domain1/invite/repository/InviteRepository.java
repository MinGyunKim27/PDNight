package org.example.pdnight.domain1.invite.repository;

import org.example.pdnight.domain1.common.enums.JoinStatus;
import org.example.pdnight.domain1.invite.entity.Invite;
import org.example.pdnight.domain1.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InviteRepository extends JpaRepository<Invite,Long> {
    Boolean existsByPostIdAndInviteeIdAndInviterId(Long postId,Long inviteeId, Long inviterId);

    void deleteAllByPostAndStatus(Post post, JoinStatus joinStatus);
}
