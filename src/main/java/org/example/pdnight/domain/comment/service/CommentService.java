package org.example.pdnight.domain.comment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.pdnight.domain.comment.dto.request.CommentRequestDto;
import org.example.pdnight.domain.comment.dto.response.CommentResponseDto;
import org.example.pdnight.domain.comment.entity.Comment;
import org.example.pdnight.domain.comment.repository.CommentRepository;
import org.example.pdnight.domain.common.dto.PagedResponse;
import org.example.pdnight.domain.common.enums.ErrorCode;
import org.example.pdnight.domain.common.exception.BaseException;
import org.example.pdnight.domain.common.helper.GetHelper;
import org.example.pdnight.domain.post.entity.Post;
import org.example.pdnight.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {

    private final CommentRepository commentRepository;
    private final GetHelper helper;

    //댓글 생성 메서드
    public CommentResponseDto createComment(Long postId, Long authorId, CommentRequestDto request) {
        //댓글을 기입될 게시글과, 작성자를 찾아옴 -> 없을 경우 예외 발생 -> 검증 로직
        Post foundPost = helper.getPostById(postId);
        User foundUser = helper.getUserById(authorId);

        //댓글 엔티티 생성 및 저장
        Comment comment = Comment.create(foundPost, foundUser, request.getContent());
        Comment savedComment = commentRepository.save(comment);

        return CommentResponseDto.from(savedComment);
    }

    //댓글 삭제 메서드
    @Transactional
    public void deleteCommentById(Long postId, Long id, Long authorId) {
        //댓글을 기입될 게시글과, 작성자를 찾아옴 -> 없을 경우 예외 발생 -> 검증 로직
        Post foundPost = helper.getPostById(postId);
        User foundUser = helper.getUserById(authorId);

        //댓글 검증 로직
        Comment foundComment = getCommentById(id);
        validateAuthorAndPost(authorId, postId, foundComment);

        //부모 댓글 id 기준 자식댓글 일괄 삭제 메서드
        commentRepository.deleteAllByParentId(id);
        commentRepository.delete(foundComment);
    }

    //댓글 수정 메서드
    @Transactional
    public CommentResponseDto updateCommentByDto(Long postId, Long id, Long authorId, CommentRequestDto request) {
        //댓글을 기입될 게시글과, 작성자를 찾아옴 -> 없을 경우 예외 발생 -> 검증 로직
        Post foundPost = helper.getPostById(postId);
        User foundUser = helper.getUserById(authorId);

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
        Post foundPost = helper.getPostById(postId);
        User foundUser = helper.getUserById(authorId);

        Comment foundComment = getCommentById(id);

        //대댓글 엔티티 생성 및 저장
        Comment childComment = Comment.createChild(foundPost, foundUser, request.getContent(), foundComment);
        Comment savedChildComment = commentRepository.save(childComment);

        return CommentResponseDto.from(savedChildComment);
    }

    //댓글 다건 조회 메서드 : 해당게시글의 댓글리스트
    @Transactional(readOnly = true)
    public PagedResponse<CommentResponseDto> getCommentsByPostId(Long postId, Pageable pageable) {
        //파람값으로 넘어온 게시글이 존재하는지 확인
        Post foundPost = helper.getPostById(postId);

        //1. 전체 댓글을 리스트로 반환 Id 기준으로 오름차순 정렬
        List<Comment> foundComments = commentRepository.findByPostIdOrderByIdAsc(postId);

        //2. Dto로 변환
        List<CommentResponseDto> commentDtos = foundComments.stream().map(CommentResponseDto::from).toList();

        //3. 부모 댓글 리스트만 걸러냄
        List<CommentResponseDto> parentCommentDtos = commentDtos.stream()
                .filter(commentDto -> commentDto.getParentId() == null)
                .toList();

        //4. 대댓글(자식댓글) 리스트 걸러냄
        List<CommentResponseDto> childCommentDtos = commentDtos.stream()
                .filter(commentDto -> commentDto.getParentId() != null)
                .toList();

        //5. 부모댓글을 순회 하는 반복문
        for (CommentResponseDto parent : parentCommentDtos) {
            //6. 자식 댓글을 순회해서 부모댓글 아이디와 자식댓글의 부모댓글 아이디가 일치하면 자식리스트에 추가
            childCommentDtos.stream()
                    .filter(child -> parent.getId().equals(child.getParentId()))
                    .forEach(parent::addChild);
        }

        //7. 수동 페이징
        Page<CommentResponseDto> pagedCommentDtos = new PageImpl<>(parentCommentDtos, pageable, parentCommentDtos.size());

        return PagedResponse.from(pagedCommentDtos);
    }


    // ----------------------------------- HELPER 메서드 ------------------------------------------------------ //
    // get
    private Comment getCommentById(Long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new BaseException(ErrorCode.COMMENT_NOT_FOUND));
    }

    // validate
    private void validateAuthorAndPost(Long userId, Long postId, Comment comment) {
        if (!comment.getAuthor().getId().equals(userId)) {
            throw new BaseException(ErrorCode.COMMENT_FORBIDDEN);
        }

        if (!comment.getPost().getId().equals(postId)) {
            throw new BaseException(ErrorCode.POST_NOT_MATCHED);
        }
    }

}
