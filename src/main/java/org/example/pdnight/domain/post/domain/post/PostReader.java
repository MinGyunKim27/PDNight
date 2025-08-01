package org.example.pdnight.domain.post.domain.post;

import org.example.pdnight.domain.post.enums.AgeLimit;
import org.example.pdnight.domain.post.enums.Gender;
import org.example.pdnight.domain.post.enums.JoinStatus;
import org.example.pdnight.domain.post.presentation.dto.response.InviteResponse;
import org.example.pdnight.domain.post.presentation.dto.response.PostResponse;
import org.example.pdnight.global.common.enums.JobCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface PostReader {

    Optional<Post> getPostById(Long id);

    Page<PostResponse> getMyLikePost(Long userId, Pageable pageable);

    Page<PostResponse> getConfirmedPost(Long userId, JoinStatus joinStatus, Pageable pageable);

    Page<Post> getWrittenPost(Long userId, Pageable pageable);

    Page<Post> getSuggestedPost(Long userId, Pageable pageable);

    Page<Post> findPostsBySearch(
            Pageable pageable,
            Integer maxParticipants,
            AgeLimit ageLimit,
            JobCategory jobCategoryLimit,
            Gender genderLimit
    );

    Optional<Post> findById(Long postId);

    Page<InviteResponse> getMyInvited(Long userId, Pageable pageable);

    Page<InviteResponse> getMyInvite(Long userId, Pageable pageable);
}