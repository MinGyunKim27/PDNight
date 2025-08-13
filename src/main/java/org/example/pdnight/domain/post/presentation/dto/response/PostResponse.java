package org.example.pdnight.domain.post.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.pdnight.domain.post.domain.post.Post;
import org.example.pdnight.domain.post.enums.AgeLimit;
import org.example.pdnight.domain.post.enums.Gender;
import org.example.pdnight.domain.post.enums.JoinStatus;
import org.example.pdnight.domain.post.enums.PostStatus;
import org.example.pdnight.global.common.enums.JobCategory;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor(force = true)
public class PostResponse {

    private final Long postId;
    private final Long authorId;
    private final String title;
    private final LocalDateTime timeSlot;
    private final String publicContent;
    private final PostStatus status;
    private final Integer maxParticipants;
    private final Gender genderLimit;
    private final JobCategory jobCategoryLimit;
    @Setter
    private List<String> tagList;
    private final AgeLimit ageLimit;
    private Integer acceptedParticipantsCount;
    private Integer participantsCount;
    private JoinStatus joinStatus;
    private LocalDateTime appliedAt;
    private final boolean isFirstCome;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    private PostResponse(Post post, List<String> tagNameList) {
        this.postId = post.getId();
        this.authorId = post.getAuthorId();
        this.title = post.getTitle();
        this.timeSlot = post.getTimeSlot();
        this.publicContent = post.getPublicContent();
        this.status = post.getStatus();
        this.maxParticipants = post.getMaxParticipants();
        this.genderLimit = post.getGenderLimit();
        this.jobCategoryLimit = post.getJobCategoryLimit();
        this.tagList = tagNameList;
        this.ageLimit = post.getAgeLimit();
        this.isFirstCome = post.getIsFirstCome();
        this.createdAt = post.getCreatedAt();
        this.updatedAt = post.getUpdatedAt();
    }

    private PostResponse(Post post, List<String> postTags, Integer acceptedParticipantsCount, Integer participantsCount) {
        this.postId = post.getId();
        this.authorId = post.getAuthorId();
        this.title = post.getTitle();
        this.timeSlot = post.getTimeSlot();
        this.publicContent = post.getPublicContent();
        this.status = post.getStatus();
        this.maxParticipants = post.getMaxParticipants();
        this.genderLimit = post.getGenderLimit();
        this.jobCategoryLimit = post.getJobCategoryLimit();
        this.tagList = postTags;
        this.ageLimit = post.getAgeLimit();
        this.isFirstCome = post.getIsFirstCome();
        this.acceptedParticipantsCount = acceptedParticipantsCount;
        this.participantsCount = participantsCount;
        this.createdAt = post.getCreatedAt();
        this.updatedAt = post.getUpdatedAt();
    }

    public static PostResponse toDtoWithCount(Post post, List<String> postTags, int acceptedParticipantsCount, int participantsCount) {
        return new PostResponse(post, postTags, acceptedParticipantsCount, participantsCount);
    }

    public static PostResponse from(Post post, List<String> tagNameList) {
        return new PostResponse(post, tagNameList);
    }

    @QueryProjection
    public PostResponse(
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
            boolean isFirstCome,
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
        this.isFirstCome = isFirstCome;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    @QueryProjection
    public PostResponse(
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
            boolean isFirstCome,
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
        this.isFirstCome = isFirstCome;
        this.joinStatus = joinStatus;
        this.appliedAt = appliedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
