package org.example.pdnight.domain.post.domain.invite;

import org.example.pdnight.domain.post.domain.post.Post;
import org.example.pdnight.domain.post.enums.JoinStatus;

public interface InviteCommandQuery {
    void save(Invite invite);

    void delete(Invite invite);

    void deleteAllByPostAndStatus(Post post, JoinStatus joinStatus);
}
