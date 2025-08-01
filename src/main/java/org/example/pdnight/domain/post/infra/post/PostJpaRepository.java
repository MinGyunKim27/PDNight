package org.example.pdnight.domain.post.infra.post;


import org.example.pdnight.domain.post.domain.post.Post;
import org.example.pdnight.domain.post.enums.PostStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostJpaRepository extends JpaRepository<Post, Long> {

    Optional<Post> findByIdAndStatus(Long id, PostStatus status);

    boolean existsById(Long id);

}