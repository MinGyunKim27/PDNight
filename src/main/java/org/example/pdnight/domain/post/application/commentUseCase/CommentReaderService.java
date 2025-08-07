package org.example.pdnight.domain.post.application.commentUseCase;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.post.domain.comment.Comment;
import org.example.pdnight.domain.post.domain.comment.CommentReader;
import org.example.pdnight.domain.post.presentation.dto.response.CommentResponse;
import org.example.pdnight.global.common.dto.PagedResponse;
import org.example.pdnight.global.common.enums.ErrorCode;
import org.example.pdnight.global.common.exception.BaseException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentReaderService {

    private final CommentReader commentReader;
    private final PostPort postPort;

    //댓글 다건 조회 메서드 : 해당게시글의 댓글리스트
    @Transactional(readOnly = true)
    public PagedResponse<CommentResponse> getCommentsByPostId(Long postId, Pageable pageable) {

        validateIsExistPost(postId);

        //1. 전체 댓글을 리스트로 반환 Id 기준으로 오름차순 정렬
        List<Comment> foundComments = commentReader.findByPostIdOrderByIdAsc(postId);

        //2. Dto로 변환
        List<CommentResponse> commentDtos = foundComments.stream().map(CommentResponse::from).toList();

        //3. 부모 댓글 리스트만 걸러냄
        List<CommentResponse> parentCommentDtos = commentDtos.stream()
                .filter(commentDto -> commentDto.getParentId() == null)
                .toList();

        //4. 대댓글(자식댓글) 리스트 걸러냄
        List<CommentResponse> childCommentDtos = commentDtos.stream()
                .filter(commentDto -> commentDto.getParentId() != null)
                .toList();

        //5. 부모댓글을 순회 하는 반복문
        for (CommentResponse parent : parentCommentDtos) {
            //6. 자식 댓글을 순회해서 부모댓글 아이디와 자식댓글의 부모댓글 아이디가 일치하면 자식리스트에 추가
            childCommentDtos.stream()
                    .filter(child -> parent.getId().equals(child.getParentId()))
                    .forEach(parent::addChild);
        }

        //7. 수동 페이징
        Page<CommentResponse> pagedCommentDtos = new PageImpl<>(parentCommentDtos, pageable, parentCommentDtos.size());

        return PagedResponse.from(pagedCommentDtos);
    }

    //----------------------------------- HELPER 메서드 ------------------------------------------------------
    // validate
    private void validateIsExistPost(Long postId) {
        if (!postPort.existsById(postId)) {
            throw new BaseException(ErrorCode.POST_NOT_FOUND);
        }
    }

}
