package org.example.pdnight.domain.post.application.PostUseCase;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.post.domain.post.Post;
import org.example.pdnight.domain.post.domain.post.PostParticipant;
import org.example.pdnight.domain.post.domain.post.PostReader;
import org.example.pdnight.domain.post.enums.JoinStatus;
import org.example.pdnight.domain.post.enums.PostStatus;
import org.example.pdnight.domain.post.presentation.dto.response.ParticipantResponse;
import org.example.pdnight.domain.post.presentation.dto.response.PostResponseWithApplyStatusDto;
import org.example.pdnight.domain.post.presentation.dto.response.PostWithJoinStatusAndAppliedAtResponseDto;
import org.example.pdnight.global.common.dto.PagedResponse;
import org.example.pdnight.global.common.enums.ErrorCode;
import org.example.pdnight.global.common.exception.BaseException;

import org.example.pdnight.global.constant.CacheName;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class PostReaderService {

    private final PostReader postReader;

    //본인이 작성한 게시글 신청자 목록 조회
    public PagedResponse<ParticipantResponse> getParticipantListByPending(Long authorId, Long postId, int page, int size) {
        Post post = getPost(postId);

        // 신청자 조회 안됨 : 게시글이 본인것이 아님
        validateMyPost(post, authorId);

        Pageable pageable = PageRequest.of(page, size);
        Page<PostParticipant> postParticipant = postReader.findByPostAndStatus(post, JoinStatus.PENDING, pageable);

        return PagedResponse.from(postParticipant.map(p -> ParticipantResponse.from(
                p.getUserId(),
                p.getPost().getId(),
                p.getStatus(),
                p.getCreatedAt(),
                p.getUpdatedAt()
        )));
    }

    //수락한 참가자 조회
    public PagedResponse<ParticipantResponse> getParticipantListByAccepted(Long loginId, Long postId, int page, int size) {
        Post post = getPost(postId);

        // 참가자 조회 안됨 : 게시글 주인이 아니거나, 참가되지 않은 사람들이 조회하는 경우 (미신청, 신청 대기, 거부)
        validateGetParticipantList(post, loginId);

        Pageable pageable = PageRequest.of(page, size);
        Page<PostParticipant> postParticipant = postReader.findByPostAndStatus(post, JoinStatus.ACCEPTED, pageable);

        return PagedResponse.from(postParticipant.map(p -> ParticipantResponse.from(
                p.getUserId(),
                p.getPost().getId(),
                p.getStatus(),
                p.getCreatedAt(),
                p.getUpdatedAt()
        )));
    }

    // 단건 조회
    @Transactional(readOnly = true)
    @Cacheable(value = CacheName.ONE_POST, key = "#id")
    public PostResponseWithApplyStatusDto findOpenedPost(Long id) {
        return postReader.getPostById(id);
    }

    // 내가 좋아요 누른 게시물 조회
    @Cacheable(
            value = CacheName.LIKED_POST,
            key = "{#pageable.pageNumber, #pageable.pageSize, #userId}"
    )
    public PagedResponse<PostResponseWithApplyStatusDto> findMyLikedPosts(Long userId, Pageable pageable) {
        Page<PostResponseWithApplyStatusDto> myLikePost = postReader.getMyLikePost(userId, pageable);
        return PagedResponse.from(myLikePost);
    }

    // 내 성사된/ 신청한 게시물 조회
    @Cacheable(
            value = CacheName.CONFIRMED_POST,
            key = "{#pageable.pageNumber, #pageable.pageSize, #userId, #joinStatus}"
    )

    public PagedResponse<PostWithJoinStatusAndAppliedAtResponseDto> findMyConfirmedPosts(Long userId, JoinStatus joinStatus, Pageable pageable) {
        Page<PostWithJoinStatusAndAppliedAtResponseDto> myLikePost = postReader.getConfirmedPost(userId, joinStatus, pageable);
        return PagedResponse.from(myLikePost);
    }

    //내 작성 게시물 조회
    @Cacheable(
            value = CacheName.WRITTEN_POST,
            key = "{#pageable.pageNumber, #pageable.pageSize, #userId}"
    )
    public PagedResponse<PostResponseWithApplyStatusDto> findMyWrittenPosts(Long userId, Pageable pageable) {
        Page<PostResponseWithApplyStatusDto> myLikePost = postReader.getWrittenPost(userId, pageable);
        return PagedResponse.from(myLikePost);
    }

    //추천 게시물 조회
    @Cacheable(
            value = CacheName.SUGGESTED_POST,
            key = "{#pageable.pageNumber, #pageable.pageSize, #userId}"
    )
    public PagedResponse<PostResponseWithApplyStatusDto> getSuggestedPosts(Long userId, Pageable pageable) {
        Page<PostResponseWithApplyStatusDto> suggestedPost = postReader.getSuggestedPost(userId, pageable);
        return PagedResponse.from(suggestedPost);
    }

    // ===========================================validate==========================================================
    // 참가자 목록 조회 용 검증 로직
    private void validateGetParticipantList(Post post, Long loginId) {
        if (!post.getAuthorId().equals(loginId) && getParticipantByStatus(loginId, post, JoinStatus.ACCEPTED) == null) {
            throw new BaseException(ErrorCode.NO_VIEWING_PERMISSION);
        }
    }

    private void validateMyPost(Post post, Long authorId) {
        if (!post.getAuthorId().equals(authorId)) {
            throw new BaseException(ErrorCode.NO_VIEWING_PERMISSION);
        }
    }

    private Post getPost(Long postId) {
        return postReader.findById(postId)
                .orElseThrow(() -> new BaseException(ErrorCode.POST_NOT_FOUND));
    }

    private Post getPostWithOpen(Long postId) {
        return postReader.findByIdAndStatus(postId, PostStatus.OPEN)
                .orElseThrow(() -> new BaseException(ErrorCode.POST_NOT_FOUND));
    }

    private PostParticipant getParticipantByStatus(Long userId, Post post, JoinStatus pending) {
        return postReader.findByUserAndPost(userId, post).stream()
                .filter(p -> p.getStatus().equals(pending))
                .findFirst()
                .orElse(null);
    }

}
