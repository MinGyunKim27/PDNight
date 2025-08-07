package org.example.pdnight.domain.post.infra.post;


import org.example.pdnight.domain.post.domain.post.Post;
import org.example.pdnight.domain.post.enums.PostStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PostJpaRepository extends JpaRepository<Post, Long> {

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.postParticipants WHERE p.id = :id AND p.status = :status")
    Optional<Post> findByIdAndStatus(Long id, PostStatus status);

    boolean existsById(Long id);

    List<Post> findAllByAuthorId(Long authorId);
}