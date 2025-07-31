package org.example.pdnight.domain.post.application.commentUseCase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.pdnight.domain.post.domain.comment.Comment;
import org.example.pdnight.domain.post.domain.comment.CommentCommander;
import org.example.pdnight.domain.post.domain.post.Post;
import org.example.pdnight.domain.post.presentation.dto.request.CommentRequestDto;
import org.example.pdnight.domain.post.presentation.dto.response.CommentResponseDto;
import org.example.pdnight.global.common.enums.ErrorCode;
import org.example.pdnight.global.common.exception.BaseException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentCommanderService {

	private final CommentCommander commentCommandQuery;

	//댓글 생성 메서드
	public CommentResponseDto createComment(Long postId, Long loginId, CommentRequestDto request) {

		//PostClient로 해당 게시물 조회

		//댓글을 기입될 게시글 없을 경우 예외 발생

		//댓글 엔티티 생성 및 저장
		Comment comment = Comment.create(postId, loginId, request.getContent());
		Comment savedComment = commentCommandQuery.save(comment);

		return CommentResponseDto.from(savedComment);
	}

	//댓글 삭제 메서드
	@Transactional
	public void deleteCommentById(Long postId, Long id, Long loginId) {
		//댓글을 기입될 게시글 찾아옴 -> 없을 경우 예외 발생 -> 검증 로직
//		getPostByIdOrElseThrow(postId);

		// Todo: PostClient로 해당 게시물 조회

		//댓글을 기입될 게시글 없을 경우 예외 발생

		//댓글 검증 로직
		Comment foundComment = getCommentById(id);
		validateComment(loginId, postId, foundComment);

		//부모 댓글 id 기준 자식댓글 일괄 삭제 메서드
		commentCommandQuery.deleteAllByParentId(id);
		commentCommandQuery.delete(foundComment);
	}

	//댓글 수정 메서드
	@Transactional
	public CommentResponseDto updateCommentByDto(Long postId, Long id, Long loginId, CommentRequestDto request) {
		//댓글을 기입될 게시글과, 작성자를 찾아옴 -> 없을 경우 예외 발생 -> 검증 로직
//		getPostByIdOrElseThrow(postId);

		//댓글 검증 로직
		Comment foundComment = getCommentById(id);
		validateComment(loginId, postId, foundComment);

		if (foundComment.getContent().equals(request.getContent())) {
			log.info("요청과 기존 댓글 내용이 동일하여 업데이트를 생략합니다. commentId = {}", foundComment.getId());
			return CommentResponseDto.from(foundComment);
		}

		foundComment.updateContent(request.getContent());
		return CommentResponseDto.from(foundComment);
	}

	//대댓글 생성 메서드
	public CommentResponseDto createChildComment(Long postId, Long id, Long loginId, CommentRequestDto request) {
		//댓글을 기입될 게시글과, 작성자를 찾아옴 -> 없을 경우 예외 발생 -> 검증 로직
//		Post foundPost = getPostByIdOrElseThrow(postId);

		Comment foundComment = getCommentById(id);

		//대댓글 엔티티 생성 및 저장
		Comment childComment = Comment.createChild(postId, loginId, request.getContent(), foundComment);
		Comment savedChildComment = commentCommandQuery.save(childComment);

		return CommentResponseDto.from(savedChildComment);
	}

	//어드민 권한 댓글 삭제 메서드
	@Transactional
	public void deleteCommentByAdmin(Long postId, Long id, Long adminId) {
		//해당 게시글이 있는지 검증
//		validateExistPost(postId);

		//해당 댓글이 있는지 검증
		Comment foundComment = getCommentById(id);

		if(!foundComment.getPostId().equals(postId)) {
			throw new BaseException(ErrorCode.POST_NOT_MATCHED);
		}

		//부모 댓글 id 기준 자식댓글 일괄 삭제 메서드
		commentCommandQuery.deleteAllByParentId(id);
		commentCommandQuery.delete(foundComment);
		log.info("{}번 Id 관리자가 댓글을 삭제했습니다.", adminId);
	}

	//웹클라이언트로 받아온 Post 꺼내는 메서드
	private Post getPostByIdOrElseThrow(Optional<Post> optionalPost)  {
		return optionalPost.orElseThrow(() -> new BaseException(ErrorCode.POST_NOT_FOUND));
	}

	private Comment getCommentById(Long id) {
		return commentCommandQuery.findById(id)
				.orElseThrow(() -> new BaseException(ErrorCode.COMMENT_NOT_FOUND));
	}

	// validate
	private void validateComment(Long loginId, Long postId, Comment comment) {
		if (!comment.getAuthorId().equals(loginId)) {
			throw new BaseException(ErrorCode.COMMENT_FORBIDDEN);
		}

		if (!comment.getPostId().equals(postId)) {
			throw new BaseException(ErrorCode.POST_NOT_MATCHED);
		}
	}

}