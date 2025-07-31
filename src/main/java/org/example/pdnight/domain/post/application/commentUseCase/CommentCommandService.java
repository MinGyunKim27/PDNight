package org.example.pdnight.domain.post.application.commentUseCase;

import java.util.Optional;

import org.example.pdnight.domain.post.domain.comment.Comment;
import org.example.pdnight.domain.post.domain.comment.CommentCommandQuery;
import org.example.pdnight.domain.post.presentation.dto.request.CommentRequestDto;
import org.example.pdnight.domain.post.presentation.dto.response.CommentResponseDto;
import org.example.pdnight.domain1.common.enums.ErrorCode;
import org.example.pdnight.domain1.common.exception.BaseException;
import org.example.pdnight.domain1.post.entity.Post;
import org.example.pdnight.domain1.user.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentCommandService {

	private final CommentCommandQuery commentCommandQuery;

	//댓글 생성 메서드
	public CommentResponseDto createComment(Long postId, Long authorId, CommentRequestDto request) {
		//댓글을 기입될 게시글과, 작성자를 찾아옴 -> 없을 경우 예외 발생 -> 검증 로직
		Post foundPost = getPostByIdOrElseThrow(postId);

		//댓글 엔티티 생성 및 저장
		Comment comment = Comment.create(foundPost, authorId, request.getContent());
		Comment savedComment = commentCommandQuery.save(comment);

		return CommentResponseDto.from(savedComment);
	}

	//댓글 삭제 메서드
	@Transactional
	public void deleteCommentById(Long postId, Long id, Long authorId) {
		//댓글을 기입될 게시글과, 작성자를 찾아옴 -> 없을 경우 예외 발생 -> 검증 로직
		getPostByIdOrElseThrow(postId);
		getUserByIdOrElseThrow(authorId);

		//댓글 검증 로직
		Comment foundComment = getCommentById(id);
		validateAuthorAndPost(authorId, postId, foundComment);

		//부모 댓글 id 기준 자식댓글 일괄 삭제 메서드
		commentCommandQuery.deleteAllByParentId(id);
		commentCommandQuery.delete(foundComment);
	}

	//댓글 수정 메서드
	@Transactional
	public CommentResponseDto updateCommentByDto(Long postId, Long id, Long authorId, CommentRequestDto request) {
		//댓글을 기입될 게시글과, 작성자를 찾아옴 -> 없을 경우 예외 발생 -> 검증 로직
		getPostByIdOrElseThrow(postId);
		getUserByIdOrElseThrow(authorId);

		//댓글 검증 로직
		Comment foundComment = getCommentById(id);
		validateAuthorAndPost(authorId, postId, foundComment);

		if (foundComment.getContent().equals(request.getContent())) {
			log.info("요청과 기존 댓글 내용이 동일하여 업데이트를 생략합니다. commentId = {}", foundComment.getId());
			return CommentResponseDto.from(foundComment);
		}

		foundComment.updateContent(request.getContent());
		return CommentResponseDto.from(foundComment);
	}

	//대댓글 생성 메서드
	public CommentResponseDto createChildComment(Long postId, Long id, Long authorId, CommentRequestDto request) {
		//댓글을 기입될 게시글과, 작성자를 찾아옴 -> 없을 경우 예외 발생 -> 검증 로직
		Post foundPost = getPostByIdOrElseThrow(postId);

		Comment foundComment = getCommentById(id);

		//대댓글 엔티티 생성 및 저장
		Comment childComment = Comment.createChild(postId, authorId, request.getContent(), foundComment);
		Comment savedChildComment = commentCommandQuery.save(childComment);

		return CommentResponseDto.from(savedChildComment);
	}

	//어드민 권한 댓글 삭제 메서드
	@Transactional
	public void deleteCommentByAdmin(Long postId, Long id, Long adminId) {
		//해당 게시글이 있는지 검증
		validateExistPost(postId);

		//해당 댓글이 있는지 검증
		Comment foundComment = getCommentById(id);

		//해당 게시글에 달린 댓글인지 검증
		validateComment(postId, foundComment);

		//부모 댓글 id 기준 자식댓글 일괄 삭제 메서드
		commentCommandQuery.deleteAllByParentId(id);
		commentCommandQuery.delete(foundComment);
		log.info("{}번 Id 관리자가 댓글을 삭제했습니다.", adminId);
	}

	private Post getPostByIdOrElseThrow(Optional<Post> optionalPost)  {
		return optionalPost.orElseThrow(() -> new BaseException(ErrorCode.POST_NOT_FOUND));
	}

	// validate
	private void validateComment(Long userId, Long postId, Comment comment) {
		if (!comment.getAuthorId().equals(userId)) {
			throw new BaseException(ErrorCode.COMMENT_FORBIDDEN);
		}

		if (!comment.getPostId().equals(postId)) {
			throw new BaseException(ErrorCode.POST_NOT_MATCHED);
		}
	}

}