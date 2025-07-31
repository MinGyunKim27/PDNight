package org.example.pdnight.domain.post.domain.post;

import org.example.pdnight.domain.post.enums.AgeLimit;
import org.example.pdnight.domain.post.enums.Gender;
import org.example.pdnight.domain.post.enums.JoinStatus;
import org.example.pdnight.domain.post.enums.PostStatus;
import org.example.pdnight.domain.post.presentation.dto.response.PostResponseWithApplyStatusDto;
import org.example.pdnight.domain.post.presentation.dto.response.PostWithJoinStatusAndAppliedAtResponseDto;

import org.example.pdnight.global.common.enums.JobCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface PostReader {

    Page<PostParticipant> findByPostAndStatus(Post post, JoinStatus joinStatus, Pageable pageable);

    PostResponseWithApplyStatusDto getPostById(Long id);

    Page<PostResponseWithApplyStatusDto> getMyLikePost(Long userId, Pageable pageable);

    Page<PostWithJoinStatusAndAppliedAtResponseDto> getConfirmedPost(Long userId, JoinStatus joinStatus, Pageable pageable);

    Page<PostResponseWithApplyStatusDto> getWrittenPost(Long userId, Pageable pageable);

    Page<PostResponseWithApplyStatusDto> getSuggestedPost(Long userId, Pageable pageable);

    Optional<Post> findById(Long postId);

    Optional<Post> findByIdAndStatus(Long postId, PostStatus postStatus);

    List<PostParticipant> findByUserAndPost(Long userId, Post post);

    Page<PostResponseWithApplyStatusDto> findPostDtosBySearch(
            Pageable pageable,
            Integer maxParticipants,
            AgeLimit ageLimit,
            JobCategory jobCategoryLimit,
            Gender genderLimit
    );

}