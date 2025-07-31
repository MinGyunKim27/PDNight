package org.example.pdnight.domain.post.domain.comment;

import java.util.List;

import org.example.pdnight.domain1.comment.entity.Comment;

public interface CommentReader {

	List<Comment> findByPostIdOrderByIdAsc(Long postId);

}