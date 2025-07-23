package org.example.pdnight.domain.comment.repository;

import org.example.pdnight.domain.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
