package org.example.pdnight.domain.post.domain.post;

import org.example.pdnight.domain.post.enums.AgeLimit;
import org.example.pdnight.domain.post.enums.Gender;
import org.example.pdnight.domain.post.enums.JoinStatus;
import org.example.pdnight.domain.post.enums.PostStatus;
import org.example.pdnight.domain.post.presentation.dto.response.InviteResponseDto;
import org.example.pdnight.domain.post.presentation.dto.response.PostResponseDto;


import org.example.pdnight.global.common.dto.PagedResponse;
import org.example.pdnight.global.common.enums.JobCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface PostReader {

    Optional<Post> getPostById(Long id);

    Page<PostResponseDto> getMyLikePost(Long userId, Pageable pageable);

    Page<PostResponseDto> getConfirmedPost(Long userId, JoinStatus joinStatus, Pageable pageable);

    Page<Post> getWrittenPost(Long userId, Pageable pageable);

    Page<Post> getSuggestedPost(Long userId, Pageable pageable);

    Page<Post> findPostDtosBySearch(
            Pageable pageable,
            Integer maxParticipants,
            AgeLimit ageLimit,
            JobCategory jobCategoryLimit,
            Gender genderLimit
    );

    Optional<Post> findById(Long postId);

    PagedResponse<InviteResponseDto> getMyinvited(Long userId, Pageable pageable);

    PagedResponse<InviteResponseDto> getMyInvite(Long userId, Pageable pageable);
}