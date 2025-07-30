package org.example.pdnight.domain1.post.repository;

import org.example.pdnight.domain1.post.entity.Post;
import org.example.pdnight.domain1.post.enums.PostStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    //상태값 조건 쿼리메서드
    Optional<Post> findByIdAndStatus(Long id, PostStatus status);

	boolean existsById(Long id);

    Optional<Post> findPostById(Long id);
}
