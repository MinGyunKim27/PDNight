package org.example.pdnight.domain.post.domain.post;

import org.example.pdnight.domain.post.enums.AgeLimit;
import org.example.pdnight.domain.post.enums.Gender;
import org.example.pdnight.domain.post.enums.JoinStatus;
import org.example.pdnight.global.common.enums.JobCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface PostReader {

    Page<Post> getSuggestedPost(Long userId, Pageable pageable);

    Optional<PostDocument> findByIdES(Long postId);

    Page<PostDocument> findPostsBySearchES(
            Pageable pageable,
            Integer maxParticipants,
            AgeLimit ageLimit,
            JobCategory jobCategoryLimit,
            Gender genderLimit
    );

    Page<PostDocument> getMyInvitedES(Long userId, Pageable pageable);

    List<PostDocument> getMyInviteES(Long userId);

    Page<PostDocument> getMyLikePostES(Long userId, Pageable pageable);

    Page<PostDocument> getConfirmedPostES(Long userId, JoinStatus joinStatus, Pageable pageable);

    Page<PostDocument> getWrittenPostES(Long userId, Pageable pageable);

    Optional<Post> findById(Long postId);
}