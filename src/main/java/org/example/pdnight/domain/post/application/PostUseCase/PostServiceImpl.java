package org.example.pdnight.domain.post.application.PostUseCase;

import lombok.RequiredArgsConstructor;

import org.example.pdnight.domain.post.enums.AgeLimit;
import org.example.pdnight.domain.post.enums.Gender;
import org.example.pdnight.domain.post.enums.JobCategory;
import org.example.pdnight.domain.post.enums.JoinStatus;
import org.example.pdnight.domain.post.presentation.dto.request.PostRequestDto;
import org.example.pdnight.domain.post.presentation.dto.request.PostStatusRequestDto;
import org.example.pdnight.domain.post.presentation.dto.request.PostUpdateRequestDto;
import org.example.pdnight.domain.post.presentation.dto.response.*;
import org.example.pdnight.domain1.common.dto.PagedResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService{

    private final PostQueryService postQueryService;
    private final PostCommandService postCommandService;

    @Override
    public PagedResponse<ParticipantResponse> getParticipantListByPending(Long authorId, Long postId, int page, int size) {
        return postQueryService.getParticipantListByPending(authorId, postId, page, size);
    };

    @Override
    public PagedResponse<ParticipantResponse> getParticipantListByAccepted(Long loginId, Long postId, int page, int size) {
        return postQueryService.getParticipantListByAccepted(loginId, postId, page, size);
    };

    @Override
    public PostResponseWithApplyStatusDto findOpenedPost(Long id) {
        return postQueryService.findOpenedPost(id);
    };

    @Override
    public PagedResponse<PostResponseWithApplyStatusDto> findMyLikedPosts(Long userId, Pageable pageable) {
        return postQueryService.findMyLikedPosts(userId, pageable);
    };

    @Override
    public PagedResponse<PostWithJoinStatusAndAppliedAtResponseDto> findMyConfirmedPosts(Long userId, JoinStatus joinStatus, Pageable pageable) {
        return postQueryService.findMyConfirmedPosts(userId, joinStatus, pageable);
    };

    @Override
    public PagedResponse<PostResponseWithApplyStatusDto> findMyWrittenPosts(Long userId, Pageable pageable) {
        return postQueryService.findMyWrittenPosts(userId, pageable);
    };

    @Override
    public PagedResponse<PostResponseWithApplyStatusDto> getSuggestedPosts(Long userId, Pageable pageable) {
        return postQueryService.getSuggestedPosts(userId, pageable);
    };

    @Override
    public ParticipantResponse applyParticipant(Long loginId, Long postId){
        return postCommandService.applyParticipant(loginId, postId);
    };

    @Override
    public void deleteParticipant(Long loginId, Long postId){
        return postCommandService.deleteParticipant(loginId, postId);
    };

    @Override
    public ParticipantResponse changeStatusParticipant(Long authorId, Long userId, Long postId, String status){
        return postCommandService.changeStatusParticipant(authorId, userId, postId, status);
    };

    @Override
    public PostCreateAndUpdateResponseDto createPost(Long userId, PostRequestDto request) {
        return postCommandService.createPost(userId, request);
    };

    @Override
    public PagedResponse<PostResponseWithApplyStatusDto> getPostDtosBySearch(
            Pageable pageable,
            Integer maxParticipants,
            AgeLimit ageLimit,
            JobCategory jobCategoryLimit,
            Gender genderLimit
    ) {
        return postCommandService.getPostDtosBySearch(pageable, maxParticipants, ageLimit, jobCategoryLimit, genderLimit);
    };

    @Override
    public void deletePostById(Long userId, Long id) {
        return postCommandService.deletePostById(userId, id);
    };

    @Override
    public PostCreateAndUpdateResponseDto updatePostDetails(Long userId, Long id, PostUpdateRequestDto request) {
        return postCommandService.updatePostDetails(userId, id, request);
    };

    @Override
    public PostResponseDto changeStatus(Long userId, Long id, PostStatusRequestDto request) {
        return postCommandService.changeStatus(userId, id, request);
    };

    @Override
    public PostLikeResponse addLike(Long id, Long userId) {
        return postCommandService.addLike(id, userId);
    };

    @Override
    public void removeLike(Long id, Long userId) {
        return postCommandService.removeLike(id, userId);
    }

    @Override
    public void deleteAdminPostById(Long id) {

    }

    ;


}
