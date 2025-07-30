package org.example.pdnight.domain1.comment.repository;

import java.util.List;

import org.example.pdnight.domain1.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentRepository extends JpaRepository<Comment, Long> {
	List<Comment> findByPostIdOrderByIdAsc(Long postId);

	@Modifying
	@Query("delete from Comment c where c.parent.id = :parentId")
	void deleteAllByParentId(@Param("parentId") Long parentId);

	//해당 게시글의 자식댓글 일괄 삭제
	@Modifying
	@Query("delete from Comment c where c.post.id = :postId and c.parent is not null")
	void deleteAllByChildrenPostId(@Param("postId") Long postId);

	//해당 게시글의 부모댓글 일괄 삭제
	@Modifying
	@Query("delete from Comment c where c.post.id = :postId and c.parent is null")
	void deleteAllByPostId(@Param("postId") Long postId);
}
