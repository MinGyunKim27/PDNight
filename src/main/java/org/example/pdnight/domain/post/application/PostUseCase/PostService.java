package org.example.pdnight.domain.post.application.PostUseCase;

import org.example.pdnight.domain.post.enums.AgeLimit;
import org.example.pdnight.domain.post.enums.Gender;
import org.example.pdnight.domain.post.enums.JoinStatus;
import org.example.pdnight.domain.post.presentation.dto.request.PostRequest;
import org.example.pdnight.domain.post.presentation.dto.request.PostStatusRequest;
import org.example.pdnight.domain.post.presentation.dto.request.PostUpdateRequest;
import org.example.pdnight.domain.post.presentation.dto.response.*;
import org.example.pdnight.global.common.dto.PagedResponse;
import org.example.pdnight.global.common.enums.JobCategory;
import org.springframework.data.domain.Pageable;

public interface PostService {

    //----------읽기 메서드
    PagedResponse<ParticipantResponse> getParticipantListByPending(Long authorId, Long postId, int page, int size);

    PagedResponse<ParticipantResponse> getParticipantListByAccepted(Long loginId, Long postId, int page, int size);

    PostResponse findPost(Long id);

    PagedResponse<PostResponse> getPostDtosBySearch(
            Pageable pageable,
            Integer maxParticipants,
            AgeLimit ageLimit,
            JobCategory jobCategoryLimit,
            Gender genderLimit
    );

    PagedResponse<PostResponse> findMyLikedPosts(Long userId, Pageable pageable);

    PagedResponse<PostResponse> findMyConfirmedPosts(Long userId, JoinStatus joinStatus, Pageable pageable);

    PagedResponse<PostResponse> findMyWrittenPosts(Long userId, Pageable pageable);

    PagedResponse<InviteResponse> getMyInvited(Long userId, Pageable pageable);

    PagedResponse<InviteResponse> getMyInvite(Long userId, Pageable pageable);



    //----------쓰기 메서드
    void deleteAdminPostById(Long id);

    void deleteParticipant(Long loginId, Long postId);

    ParticipantResponse changeStatusParticipant(Long authorId, Long userId, Long postId, String status);

    PostResponse createPost(Long userId, PostRequest request);

    PagedResponse<PostResponse> getSuggestedPosts(Long userId, Pageable pageable);

    ParticipantResponse applyParticipant(Long loginId, Long age, Gender gender, JobCategory jobCategory, Long postId);

    void deletePostById(Long userId, Long id);

    PostResponse updatePostDetails(Long userId, Long id, PostUpdateRequest request);

    PostResponse changeStatus(Long userId, Long id, PostStatusRequest request);

    PostLikeResponse addLike(Long id, Long userId);

    void removeLike(Long id, Long userId);

    InviteResponse createInvite(Long postId, Long userId, Long loginUserId);

    void deleteInvite(Long postId, Long userId, Long loginUserId);

    void acceptForInvite(Long postId, Long loginUserId);

    void rejectForInvite(Long postId, Long loginUserId);
}