package org.example.pdnight.domain.post.domain.comment;

import java.util.List;

public interface CommentReader {

	List<Comment> findByPostIdOrderByIdAsc(Long postId);

}