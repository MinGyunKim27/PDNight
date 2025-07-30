package org.example.pdnight.domain.post.application.PostUseCase;


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

public interface PostService {

    PagedResponse<ParticipantResponse> getParticipantListByPending(Long authorId, Long postId, int page, int size);

    PagedResponse<ParticipantResponse> getParticipantListByAccepted(Long loginId, Long postId, int page, int size);

    PostResponseWithApplyStatusDto findOpenedPost(Long id);

    PagedResponse<PostResponseWithApplyStatusDto> findMyLikedPosts(Long userId, Pageable pageable);

    PagedResponse<PostWithJoinStatusAndAppliedAtResponseDto> findMyConfirmedPosts(Long userId, JoinStatus joinStatus, Pageable pageable);

    PagedResponse<PostResponseWithApplyStatusDto> findMyWrittenPosts(Long userId, Pageable pageable);

    PagedResponse<PostResponseWithApplyStatusDto> getSuggestedPosts(Long userId, Pageable pageable);

    ParticipantResponse applyParticipant(Long loginId, Long postId);

    void deleteParticipant(Long loginId, Long postId);

    ParticipantResponse changeStatusParticipant(Long authorId, Long userId, Long postId, String status);

    PostCreateAndUpdateResponseDto createPost(Long userId, PostRequestDto request);

    PagedResponse<PostResponseWithApplyStatusDto> getPostDtosBySearch(
            Pageable pageable,
            Integer maxParticipants,
            AgeLimit ageLimit,
            JobCategory jobCategoryLimit,
            Gender genderLimit
    );

    void deletePostById(Long userId, Long id);

    PostCreateAndUpdateResponseDto updatePostDetails(Long userId, Long id, PostUpdateRequestDto request);

    PostResponseDto changeStatus(Long userId, Long id, PostStatusRequestDto request);

    PostLikeResponse addLike(Long id, Long userId);

    void removeLike(Long id, Long userId);

    void deleteAdminPostById(Long id);
}