package org.example.pdnight.domain.post.repository;

import org.example.pdnight.domain.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}
