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
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {

	private final CommentRepository commentRepository;
	private final PostRepository postRepository;
	private final UserRepository userRepository;

	//댓글 생성 메서드
	public CommentResponseDto createComment(Long postId, Long authorId, CommentRequestDto request) {
		//댓글을 기입될 게시글과, 작성자를 찾아옴 -> 없을 경우 예외 발생 -> 검증 로직
		Post foundPost = getPostOrThrow(postRepository.findById(postId));
		User foundUser = getUserOrThrow(userRepository.findByIdAndIsDeletedFalse(authorId));

		//댓글 엔티티 생성 및 저장
		Comment comment = Comment.create(foundPost, foundUser, request.getContent());
		Comment savedComment = commentRepository.save(comment);

		return CommentResponseDto.from(savedComment);
	}

	//댓글 삭제 메서드
	@Transactional
	public void deleteCommentById(Long postId, Long id, Long authorId) {
		//댓글을 기입될 게시글과, 작성자를 찾아옴 -> 없을 경우 예외 발생 -> 검증 로직
		Post foundPost = getPostOrThrow(postRepository.findById(postId));
		User foundUser = getUserOrThrow(userRepository.findByIdAndIsDeletedFalse(authorId));

		//댓글 검증 로직
		Comment foundComment = getCommentOrThrow(commentRepository.findById(id));
		validateAuthorAndPost(authorId, postId, foundComment);

		commentRepository.delete(foundComment);
	}

	//댓글 수정 메서드
	@Transactional
	public CommentResponseDto updateCommentByDto(Long postId, Long id, Long authorId, CommentRequestDto request) {
		//댓글을 기입될 게시글과, 작성자를 찾아옴 -> 없을 경우 예외 발생 -> 검증 로직
		Post foundPost = getPostOrThrow(postRepository.findById(postId));
		User foundUser = getUserOrThrow(userRepository.findByIdAndIsDeletedFalse(authorId));

		//댓글 검증 로직
		Comment foundComment = getCommentOrThrow(commentRepository.findById(id));
		validateAuthorAndPost(authorId, postId, foundComment);

		if(foundComment.getContent().equals(request.getContent())) {
			log.info("요청과 기존 댓글 내용이 동일하여 업데이트를 생략합니다. commentId = {}", foundComment.getId());
			return CommentResponseDto.from(foundComment);
		}

		foundComment.updateContent(request.getContent());
		return CommentResponseDto.from(foundComment);
	}

	//대댓글 생성 메서드
	public CommentResponseDto createChildComment(Long postId, Long id, Long authorId, CommentRequestDto request) {
		//댓글을 기입될 게시글과, 작성자를 찾아옴 -> 없을 경우 예외 발생 -> 검증 로직
		Post foundPost = getPostOrThrow(postRepository.findById(postId));
		User foundUser = getUserOrThrow(userRepository.findByIdAndIsDeletedFalse(authorId));

		Comment foundComment = getCommentOrThrow(commentRepository.findById(id));

		//대댓글 엔티티 생성 및 저장
		Comment childComment = Comment.createChild(foundPost, foundUser, request.getContent(), foundComment);
		Comment savedChildComment = commentRepository.save(childComment);

		return CommentResponseDto.from(savedChildComment);
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

	//댓글 검증 메서드
	private void validateAuthorAndPost(Long userId, Long postId, Comment comment) {
		if (!comment.getAuthor().getId().equals(userId)) {
			throw new BaseException(ErrorCode.COMMENT_FORBIDDEN);
		}

		if (!comment.getPost().getId().equals(postId)) {
			throw new BaseException(ErrorCode.POST_NOT_MATCHED);
		}
	}

}
