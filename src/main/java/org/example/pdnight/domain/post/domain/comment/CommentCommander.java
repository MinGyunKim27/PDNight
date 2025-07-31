package org.example.pdnight.domain.post.domain.comment;

import java.util.Optional;

public interface CommentCommander {

	void deleteAllByParentId(Long id);

	void delete(Comment foundComment);

	void deleteAllByChildrenPostId(Long postId);

	void deleteAllByPostId(Long postId);

	Comment save(Comment comment);

	Optional<Comment> findById(Long id);

}