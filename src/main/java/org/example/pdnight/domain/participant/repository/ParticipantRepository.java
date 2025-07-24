package org.example.pdnight.domain.participant.repository;

import org.example.pdnight.domain.participant.entity.PostParticipant;
import org.example.pdnight.domain.common.enums.JoinStatus;
import org.example.pdnight.domain.post.entity.Post;
import org.example.pdnight.domain.user.entity.User;
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
