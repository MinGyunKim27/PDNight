package org.example.pdnight.domain.post.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.pdnight.domain.common.enums.JobCategory;
import org.example.pdnight.domain.post.entity.Post;
import org.example.pdnight.domain.post.enums.AgeLimit;
import org.example.pdnight.domain.post.enums.Gender;
import org.example.pdnight.domain.post.enums.PostStatus;

import java.time.LocalDateTime;
import java.util.List;


@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor(force = true)
public class PostResponseWithApplyStatusDto {
    private final Long postId;
    private final Long authorId;
    private final String title;
    private final LocalDateTime timeSlot;
    private final String publicContent;
    private final String privateContent;
    private final PostStatus status;
    private final Integer maxParticipants;
    private final Gender genderLimit;
    private final JobCategory jobCategoryLimit;
    private final AgeLimit ageLimit;
    private List<String> hobbyList;
    private List<String> techStackList;
    private final Long appliedCount;
    private final Long confirmedCount;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    private PostResponseWithApplyStatusDto(Post post, Long appliedCount, Long confirmedCount) {
        this.postId = post.getId();
        this.authorId = post.getAuthor().getId();
        this.title = post.getTitle();
        this.timeSlot = post.getTimeSlot();
        this.publicContent = post.getPublicContent();
        this.privateContent = post.getPrivateContent();
        this.status = post.getStatus();
        this.maxParticipants = post.getMaxParticipants();
        this.genderLimit = post.getGenderLimit();
        this.jobCategoryLimit = post.getJobCategoryLimit();
        this.ageLimit = post.getAgeLimit();
        this.createdAt = post.getCreatedAt();
        this.updatedAt = post.getUpdatedAt();
        this.appliedCount = appliedCount;
        this.confirmedCount = confirmedCount;
    }

    public static PostResponseWithApplyStatusDto from(Post post, Long appliedCount, Long confirmedCount) {
        return new PostResponseWithApplyStatusDto(post, appliedCount, confirmedCount);
    }

    @QueryProjection
    public PostResponseWithApplyStatusDto(
            Long id,
            Long authorId,
            String title,
            LocalDateTime timeSlot,
            String publicContent,
            String privateContent,
            PostStatus status,
            Integer maxParticipants,
            Gender genderLimit,
            JobCategory jobCategoryLimit,
            AgeLimit ageLimit,
            Long appliedCount,
            Long confirmedCount,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.postId = id;
        this.authorId = authorId;
        this.title = title;
        this.timeSlot = timeSlot;
        this.publicContent = publicContent;
        this.privateContent = privateContent;
        this.status = status;
        this.maxParticipants = maxParticipants;
        this.genderLimit = genderLimit;
        this.jobCategoryLimit = jobCategoryLimit;
        this.ageLimit = ageLimit;
        this.appliedCount = appliedCount;
        this.confirmedCount = confirmedCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public void setHobbyAndTech(List<String> hobbyList, List<String> techStackList) {
        this.hobbyList = hobbyList;
        this.techStackList = techStackList;
    }

}
