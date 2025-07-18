package org.example.pdnight.domain.post.repository;

import java.util.Optional;

import org.example.pdnight.domain.post.entity.Post;
import org.example.pdnight.domain.post.enums.PostStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
	//상태값 조건 쿼리메서드
	Optional<Post> findByIdAndStatus(Long id, PostStatus status);
}
