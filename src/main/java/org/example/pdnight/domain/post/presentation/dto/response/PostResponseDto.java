package org.example.pdnight.domain.post.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.pdnight.domain.post.domain.post.Post;
import org.example.pdnight.domain.post.enums.AgeLimit;
import org.example.pdnight.domain.post.enums.Gender;
import org.example.pdnight.domain.post.enums.JoinStatus;
import org.example.pdnight.domain.post.enums.PostStatus;
import org.example.pdnight.global.common.enums.JobCategory;


import java.time.LocalDateTime;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor(force = true)
public class PostResponseDto {

    private final Long postId;
    private final Long authorId;
    private final String title;
    private final LocalDateTime timeSlot;
    private final String publicContent;
    private final PostStatus status;
    private final Integer maxParticipants;
    private final Gender genderLimit;
    private final JobCategory jobCategoryLimit;
    private final AgeLimit ageLimit;
    private Integer acceptedParticipantsCount;
    private Integer participantsCount;
    private JoinStatus joinStatus;
    private LocalDateTime appliedAt;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    private PostResponseDto(Post post) {
        this.postId = post.getId();
        this.authorId = post.getAuthorId();
        this.title = post.getTitle();
        this.timeSlot = post.getTimeSlot();
        this.publicContent = post.getPublicContent();
        this.status = post.getStatus();
        this.maxParticipants = post.getMaxParticipants();
        this.genderLimit = post.getGenderLimit();
        this.jobCategoryLimit = post.getJobCategoryLimit();
        this.ageLimit = post.getAgeLimit();
        this.createdAt = post.getCreatedAt();
        this.updatedAt = post.getUpdatedAt();
    }

    private PostResponseDto(Post post, Integer acceptedParticipantsCount, Integer participantsCount) {
        this.postId = post.getId();
        this.authorId = post.getAuthorId();
        this.title = post.getTitle();
        this.timeSlot = post.getTimeSlot();
        this.publicContent = post.getPublicContent();
        this.status = post.getStatus();
        this.maxParticipants = post.getMaxParticipants();
        this.genderLimit = post.getGenderLimit();
        this.jobCategoryLimit = post.getJobCategoryLimit();
        this.ageLimit = post.getAgeLimit();
        this.acceptedParticipantsCount = acceptedParticipantsCount;
        this.participantsCount = participantsCount;
        this.createdAt = post.getCreatedAt();
        this.updatedAt = post.getUpdatedAt();
    }

    public static PostResponseDto toDto(Post post) {
        return new PostResponseDto(post);
    }

    public static PostResponseDto toDtoWithCount(Post post, int acceptedParticipantsCount, int participantsCount) {
        return new PostResponseDto(post, acceptedParticipantsCount, participantsCount);
    }

    @QueryProjection
    public PostResponseDto(
            Long id,
            Long authorId,
            String title,
            LocalDateTime timeSlot,
            String publicContent,
            PostStatus status,
            Integer maxParticipants,
            Gender genderLimit,
            JobCategory jobCategoryLimit,
            AgeLimit ageLimit,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.postId = id;
        this.authorId = authorId;
        this.title = title;
        this.timeSlot = timeSlot;
        this.publicContent = publicContent;
        this.status = status;
        this.maxParticipants = maxParticipants;
        this.genderLimit = genderLimit;
        this.jobCategoryLimit = jobCategoryLimit;
        this.ageLimit = ageLimit;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    @QueryProjection
    public PostResponseDto(
            Long id,
            Long authorId,
            String title,
            LocalDateTime timeSlot,
            String publicContent,
            PostStatus status,
            Integer maxParticipants,
            Gender genderLimit,
            JobCategory jobCategoryLimit,
            AgeLimit ageLimit,
            JoinStatus joinStatus,
            LocalDateTime appliedAt,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.postId = id;
        this.authorId = authorId;
        this.title = title;
        this.timeSlot = timeSlot;
        this.publicContent = publicContent;
        this.status = status;
        this.maxParticipants = maxParticipants;
        this.genderLimit = genderLimit;
        this.jobCategoryLimit = jobCategoryLimit;
        this.ageLimit = ageLimit;
        this.joinStatus = joinStatus;
        this.appliedAt = appliedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
