package org.example.pdnight.domain.comment.service;

import java.util.Optional;

import org.example.pdnight.domain.comment.dto.request.CommentRequestDto;
import org.example.pdnight.domain.comment.dto.response.CommentResponseDto;
import org.example.pdnight.domain.comment.entity.Comment;
import org.example.pdnight.domain.comment.repository.CommentRepository;
import org.example.pdnight.domain.common.enums.ErrorCode;
import org.example.pdnight.domain.common.exception.BaseException;
import org.example.pdnight.domain.post.entity.Post;
import org.example.pdnight.domain.post.repository.PostRepository;
import org.example.pdnight.domain.user.entity.User;
import org.example.pdnight.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentService {

	private final CommentRepository commentRepository;
	private final PostRepository postRepository;
	private final UserRepository userRepository;

	public CommentResponseDto createComment(Long postId, Long authorId, CommentRequestDto request) {
		//댓글을 기입될 게시글과, 작성자를 찾아옴
		Post foundPost = getPostOrThrow(postRepository.findById(postId));
		User foundUser = getUserOrThrow(userRepository.findByIdAndIsDeletedFalse(authorId));

		//댓글 엔티티 생성 및 저장
		Comment comment = Comment.create(foundPost, foundUser, request.getContent());
		Comment savedComment = commentRepository.save(comment);

		return CommentResponseDto.from(savedComment);
	}

	//이하 헬퍼 메서드
	private Comment getCommentOrThrow(Optional<Comment> comment) {
		return comment.orElseThrow(() -> new BaseException(ErrorCode.COMMENT_NOT_FOUND));
	}

	private Post getPostOrThrow(Optional<Post> post) {
		return post.orElseThrow(() -> new BaseException(ErrorCode.POST_NOT_FOUND));
	}

	private User getUserOrThrow(Optional<User> user) {
		return user.orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));
	}

	private void validateAuthor(Long userId, Post post) {
		if (!post.getAuthor().getId().equals(userId)) {
			throw new BaseException(ErrorCode.POST_FORBIDDEN);
		}
	}

}
