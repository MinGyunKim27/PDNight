package org.example.pdnight.domain.post.infra;

import org.example.pdnight.domain.post.domain.comment.CommentCommandQuery;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CommentRepositoryImpl implements CommentCommandQuery {

	private final CommentJpaRepository commentJpaRepository;

	@Override
	public void deleteAllByParentId(@Param("parentId") Long parentId){
		commentJpaRepository.deleteAllByParentId(parentId);
	}

	@Override
	public void deleteAllByChildrenPostId(@Param("postId") Long postId){
		commentJpaRepository.deleteAllByChildrenPostId(postId);
	}

	@Override
	public void deleteAllByPostId(@Param("postId") Long postId){
		commentJpaRepository.deleteAllByPostId(postId);
	}

}