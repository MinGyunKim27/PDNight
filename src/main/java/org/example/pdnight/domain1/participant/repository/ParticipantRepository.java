package org.example.pdnight.domain1.participant.repository;

import org.example.pdnight.domain1.participant.entity.PostParticipant;
import org.example.pdnight.domain1.common.enums.JoinStatus;
import org.example.pdnight.domain1.post.entity.Post;
import org.example.pdnight.domain1.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ParticipantRepository extends JpaRepository<PostParticipant, Long> {
    List<PostParticipant> findByUserAndPost(User user, Post post);

    List<PostParticipant> findByPostAndStatus(Post post, JoinStatus status);

    Page<PostParticipant> findByPostAndStatus(Post post, JoinStatus status, Pageable pageable);

    int countByPostAndStatus(Post post, JoinStatus joinStatus);
}
