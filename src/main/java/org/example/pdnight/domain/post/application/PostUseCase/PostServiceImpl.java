package org.example.pdnight.domain.post.application.PostUseCase;

import lombok.RequiredArgsConstructor;

import org.example.pdnight.domain.post.enums.AgeLimit;
import org.example.pdnight.domain.post.enums.Gender;
import org.example.pdnight.domain.post.enums.JoinStatus;
import org.example.pdnight.domain.post.presentation.dto.request.PostRequestDto;
import org.example.pdnight.domain.post.presentation.dto.request.PostStatusRequestDto;
import org.example.pdnight.domain.post.presentation.dto.request.PostUpdateRequestDto;
import org.example.pdnight.domain.post.presentation.dto.response.*;
import org.example.pdnight.global.common.dto.PagedResponse;
import org.example.pdnight.global.common.enums.JobCategory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService{

    private final PostReaderService postQueryService;
    private final PostCommanderService postCommanderService;

    @Override
    public PagedResponse<ParticipantResponse> getParticipantListByPending(Long authorId, Long postId, int page, int size) {
        return postQueryService.getParticipantListByPending(authorId, postId, page, size);
    }

    @Override
    public PagedResponse<ParticipantResponse> getParticipantListByAccepted(Long loginId, Long postId, int page, int size) {
        return postQueryService.getParticipantListByAccepted(loginId, postId, page, size);
    }

    @Override
    public PostResponseWithApplyStatusDto findOpenedPost(Long id) {
        return postQueryService.findOpenedPost(id);
    };

    @Override
    public PagedResponse<PostResponseWithApplyStatusDto> findMyLikedPosts(Long userId, Pageable pageable) {
        return postQueryService.findMyLikedPosts(userId, pageable);
    }

    @Override
    public PagedResponse<PostWithJoinStatusAndAppliedAtResponseDto> findMyConfirmedPosts(Long userId, JoinStatus joinStatus, Pageable pageable) {
        return postQueryService.findMyConfirmedPosts(userId, joinStatus, pageable);
    }

    @Override
    public PagedResponse<PostResponseWithApplyStatusDto> findMyWrittenPosts(Long userId, Pageable pageable) {
        return postQueryService.findMyWrittenPosts(userId, pageable);
    }

    @Override
    public PagedResponse<PostResponseWithApplyStatusDto> getSuggestedPosts(Long userId, Pageable pageable) {
        return postQueryService.getSuggestedPosts(userId, pageable);
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
    public ParticipantResponse changeStatusParticipant(Long authorId, Long userId, Long postId, String status){
        return postCommanderService.changeStatusParticipant(authorId, userId, postId, status);
    }

    @Override
    public PostCreateAndUpdateResponseDto createPost(Long userId, PostRequestDto request) {
        return postCommanderService.createPost(userId, request);
    }

    @Override
    public PagedResponse<PostResponseWithApplyStatusDto> getPostDtosBySearch(
            Pageable pageable,
            Integer maxParticipants,
            AgeLimit ageLimit,
            JobCategory jobCategoryLimit,
            Gender genderLimit
    ) {
        return postCommanderService.getPostDtosBySearch(pageable, maxParticipants, ageLimit, jobCategoryLimit, genderLimit);
    }

    @Override
    public void deletePostById(Long userId, Long id) {
        postCommanderService.deletePostById(userId, id);
    }

    @Override
    public PostCreateAndUpdateResponseDto updatePostDetails(Long userId, Long id, PostUpdateRequestDto request) {
        return postCommanderService.updatePostDetails(userId, id, request);
    }

    @Override
    public PostResponseDto changeStatus(Long userId, Long id, PostStatusRequestDto request) {
        return postCommanderService.changeStatus(userId, id, request);
    }

    @Override
    public PostLikeResponse addLike(Long id, Long userId) {
        return postCommanderService.addLike(id, userId);
    }

    @Override
    public void removeLike(Long id, Long userId) {
        postCommanderService.removeLike(id, userId);
    }

    @Override
    public void deleteAdminPostById(Long id) {
        postCommanderService.deleteAdminPostById(id);
    }


}
