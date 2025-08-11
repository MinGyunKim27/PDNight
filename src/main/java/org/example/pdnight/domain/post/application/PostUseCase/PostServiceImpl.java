package org.example.pdnight.domain.post.application.PostUseCase;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.post.enums.AgeLimit;
import org.example.pdnight.domain.post.enums.Gender;
import org.example.pdnight.domain.post.enums.JoinStatus;
import org.example.pdnight.domain.post.presentation.dto.request.PostRequest;
import org.example.pdnight.domain.post.presentation.dto.request.PostStatusRequest;
import org.example.pdnight.domain.post.presentation.dto.request.PostUpdateRequest;
import org.example.pdnight.domain.post.presentation.dto.response.InviteResponse;
import org.example.pdnight.domain.post.presentation.dto.response.ParticipantResponse;
import org.example.pdnight.domain.post.presentation.dto.response.PostLikeResponse;
import org.example.pdnight.domain.post.presentation.dto.response.PostResponse;
import org.example.pdnight.global.aop.SaveLog;
import org.example.pdnight.global.common.dto.PagedResponse;
import org.example.pdnight.global.common.enums.JobCategory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostReaderService postReaderService;
    private final PostCommanderService postCommanderService;

    @Override
    public PagedResponse<ParticipantResponse> getParticipantListByPending(Long authorId, Long postId, int page, int size) {
        return postReaderService.getParticipantListByPending(authorId, postId, page, size);
    }

    @Override
    public PagedResponse<ParticipantResponse> getParticipantListByAccepted(Long loginId, Long postId, int page, int size) {
        return postReaderService.getParticipantListByAccepted(loginId, postId, page, size);
    }

    @Override
    public PostResponse findPost(Long id) {
        return postReaderService.findPost(id);
    }

    @Override
    public PagedResponse<PostResponse> findMyLikedPosts(Long userId, Pageable pageable) {
        return postReaderService.findMyLikedPosts(userId, pageable);
    }

    @Override
    public PagedResponse<PostResponse> findMyConfirmedPosts(Long userId, JoinStatus joinStatus, Pageable pageable) {
        return postReaderService.findMyConfirmedPosts(userId, joinStatus, pageable);
    }

    @Override
    public PagedResponse<PostResponse> findMyWrittenPosts(Long userId, Pageable pageable) {
        return postReaderService.findMyWrittenPosts(userId, pageable);
    }

    @Override
    public PagedResponse<PostResponse> getSuggestedPosts(Long userId, Pageable pageable) {
        return postReaderService.getSuggestedPosts(userId, pageable);
    }

    @Override
    public ParticipantResponse applyParticipant(Long loginId, Long age, Gender gender, JobCategory jobCategory, Long postId) {
        return postCommanderService.applyParticipant(loginId, age, gender, jobCategory, postId);
    }

    @Override
    public void deleteParticipant(Long loginId, Long postId) {
        postCommanderService.deleteParticipant(loginId, postId);
    }

    @Override
    public ParticipantResponse changeStatusParticipant(Long authorId, Long userId, Long postId, String status) {
        return postCommanderService.changeStatusParticipant(authorId, userId, postId, status);
    }

    @Override
    public PostResponse createPost(Long userId, PostRequest request) {
        return postCommanderService.createPost(userId, request);
    }

    @Override
    public PagedResponse<PostResponse> getPostDtosBySearch(
            Pageable pageable,
            Integer maxParticipants,
            AgeLimit ageLimit,
            JobCategory jobCategoryLimit,
            Gender genderLimit
    ) {
        return postReaderService.getPostDtosBySearch(pageable, maxParticipants, ageLimit, jobCategoryLimit, genderLimit);
    }

    @Override
    public void deletePostById(Long userId, Long id) {
        postCommanderService.deletePostById(userId, id);
    }

    @Override
    public PostResponse updatePostDetails(Long userId, Long id, PostUpdateRequest request) {
        return postCommanderService.updatePostDetails(userId, id, request);
    }

    @Override
    public PostResponse changeStatus(Long userId, Long id, PostStatusRequest request) {
        return postCommanderService.changePostStatus(userId, id, request);
    }

    @Override
    public PostLikeResponse addLike(Long postId, Long userId) {
        return postCommanderService.addLike(postId, userId);
    }

    @Override
    public void removeLike(Long postId, Long userId) {
        postCommanderService.removeLike(postId, userId);
    }

    @Override
    public InviteResponse createInvite(Long postId, Long userId, Long loginUserId) {
        return postCommanderService.createInvite(postId, userId, loginUserId);
    }

    @Override
    public void deleteInvite(Long postId, Long userId, Long loginUserId) {
        postCommanderService.deleteInvite(postId, userId, loginUserId);
    }

    @Override
    public void acceptForInvite(Long postId, Long loginUserId) {
        postCommanderService.decisionForInvite(postId, loginUserId);
    }

    @Override
    public void rejectForInvite(Long postId, Long loginUserId) {
        postCommanderService.rejectForInvite(postId, loginUserId);
    }

    @Override
    public PagedResponse<InviteResponse> getMyInvited(Long userId, Pageable pageable) {
        return postReaderService.getMyInvited(userId, pageable);
    }

    @Override
    public PagedResponse<InviteResponse> getMyInvite(Long userId, Pageable pageable) {
        return postReaderService.getMyInvite(userId, pageable);
    }

    @SaveLog
    @Override
    public void deleteAdminPostById(Long id) {
        postCommanderService.deleteAdminPostById(id);
    }


}
