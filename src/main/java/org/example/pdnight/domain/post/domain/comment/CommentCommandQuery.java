package org.example.pdnight.domain.post.domain.comment;

import org.springframework.data.repository.query.Param;

public interface CommentCommandQuery {

	void deleteAllByParentId(Long id);

	void delete(Comment foundComment);

	void deleteAllByChildrenPostId(Long postId);

	void deleteAllByPostId(Long postId);

}