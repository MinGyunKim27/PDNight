package org.example.pdnight.domain.post.infra;

import java.util.List;

import org.example.pdnight.domain.post.domain.comment.CommentReader;
import org.example.pdnight.domain1.comment.entity.Comment;
import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CommentQueryRepositoryImpl implements CommentReader {

	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public List<Comment> findByPostIdOrderByIdAsc(Long postId) {
		return List.of();
	}

}