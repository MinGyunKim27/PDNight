package org.example.pdnight.domain.post.repository;

import org.example.pdnight.domain.common.enums.JobCategory;
import org.example.pdnight.domain.common.enums.JoinStatus;
import org.example.pdnight.domain.post.dto.response.PostResponseWithApplyStatusDto;
import org.example.pdnight.domain.post.dto.response.PostWithJoinStatusAndAppliedAtResponseDto;
import org.example.pdnight.domain.post.enums.AgeLimit;
import org.example.pdnight.domain.post.enums.Gender;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostRepositoryQuery {

    PostResponseWithApplyStatusDto getOpenedPostById(Long postId);

    Page<PostResponseWithApplyStatusDto> getMyLikePost(Long userId, Pageable pageable);

    Page<PostWithJoinStatusAndAppliedAtResponseDto> getConfirmedPost(Long userId, JoinStatus joinStatus, Pageable pageable);

    //필터링 조건에 따른 동적쿼리 프로젝션으로 DTO 반환
    Page<PostResponseWithApplyStatusDto> findPostDtosBySearch(
            Pageable pageable,
            Integer maxParticipants,
            AgeLimit ageLimit,
            JobCategory jobCategoryLimit,
            Gender genderLimit
    );

    Page<PostResponseWithApplyStatusDto> getWrittenPost(Long userId, Pageable pageable);

    Page<PostResponseWithApplyStatusDto> getSuggestedPost(Long id, Pageable pageable);
}
