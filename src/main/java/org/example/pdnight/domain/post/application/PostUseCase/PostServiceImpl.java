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

    private final PostCommanderService postCommanderService;
    private final PostReaderESService postReaderESService;

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
    public PagedResponse<PostResponse> getSuggestedPosts(Long userId, Pageable pageable) {
        return postReaderESService.getSuggestedPosts(userId, pageable);

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

    @SaveLog
    @Override
    public void deleteAdminPostById(Long id) {
        postCommanderService.deleteAdminPostById(id);
    }

    @Override
    public PostResponse findPostES(Long id) {
        return postReaderESService.findPostES(id);
    }

    @Override
    public PagedResponse<PostResponse> findMyWrittenPostsES(Long id, Pageable pageable) {
        return postReaderESService.findMyWrittenPostsES(id, pageable);
    }

    @Override
    public PagedResponse<PostResponse> findMyLikedPostsES(Long id, Pageable pageable) {
        return postReaderESService.findMyLikedPostsES(id, pageable);
    }

    @Override
    public PagedResponse<PostResponse> findMyConfirmedPostsES(Long id, JoinStatus joinStatus, Pageable pageable) {
        return postReaderESService.findMyConfirmedPostsES(id, joinStatus, pageable);
    }

    @Override
    public PagedResponse<PostResponse> getPostDtosBySearchES(Pageable pageable, Integer maxParticipants, AgeLimit ageLimit, JobCategory jobCategoryLimit, Gender genderLimit) {
        return postReaderESService.getPostDtosBySearchES(pageable, maxParticipants, ageLimit, jobCategoryLimit, genderLimit);
    }

    @Override
    public PagedResponse<ParticipantResponse> getParticipantListByPendingES(Long userId, Long postId, int page, int size) {
        return postReaderESService.getParticipantListByPendingES(userId, postId, page, size);
    }

    @Override
    public PagedResponse<ParticipantResponse> getParticipantListByAcceptedES(Long userId, Long postId, int page, int size) {
        return postReaderESService.getParticipantListByAcceptedES(userId, postId, page, size);
    }

    @Override
    public PagedResponse<InviteResponse> getMyInvitedES(Long userId, Pageable pageable) {
        return postReaderESService.getMyInvitedES(userId, pageable);
    }

    @Override
    public PagedResponse<InviteResponse> getMyInviteES(Long userId, Pageable pageable) {
        return postReaderESService.getMyInviteES(userId, pageable);
    }
}
