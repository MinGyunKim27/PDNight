package org.example.pdnight.domain.comment.service;

import org.example.pdnight.domain.comment.entity.Comment;
import org.example.pdnight.domain.comment.repository.CommentRepository;
import org.example.pdnight.domain.common.enums.ErrorCode;
import org.example.pdnight.domain.common.exception.BaseException;
import org.example.pdnight.domain.post.repository.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminCommentService {

	private final CommentRepository commentRepository;
	private final PostRepository postRepository;

	@Transactional
	public void deleteCommentByAdmin(Long postId, Long id, Long adminId) {
		//해당 게시글이 있는지 검증
		validateExistPost(postId);

		//해당 댓글이 있는지 검증
		Comment foundComment = getCommentById(id);

		//해당 게시글에 달린 댓글인지 검증
		validateComment(postId, foundComment);

		//부모 댓글 id 기준 자식댓글 일괄 삭제 메서드
		commentRepository.deleteAllByParentId(id);
		commentRepository.delete(foundComment);
		log.info("{}번 Id 관리자가 댓글을 삭제했습니다.", adminId);
	}


	// ----------------------------------- HELPER 메서드 ------------------------------------------------------ //
	// get
	private Comment getCommentById(Long id) {
		return commentRepository.findById(id)
				.orElseThrow(() -> new BaseException(ErrorCode.COMMENT_NOT_FOUND));
	}

	// validate
	private void validateComment(Long postId, Comment foundComment) {
		if(!foundComment.getPost().getId().equals(postId)) {
			throw new BaseException(ErrorCode.POST_NOT_MATCHED);
		}
	}

	private void validateExistPost(Long postId) {
		if(!postRepository.existsById(postId)){
			throw new BaseException(ErrorCode.POST_NOT_FOUND);
		}
	}
}
