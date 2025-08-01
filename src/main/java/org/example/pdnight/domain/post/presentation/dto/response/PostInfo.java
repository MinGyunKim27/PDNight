package org.example.pdnight.domain.post.presentation.dto.response;

import lombok.Getter;
import org.example.pdnight.domain.post.enums.AgeLimit;
import org.example.pdnight.domain.post.enums.Gender;
import org.example.pdnight.domain.post.enums.PostStatus;
import org.example.pdnight.global.common.enums.JobCategory;

import java.time.LocalDateTime;

@Getter
public class PostInfo {
    private Long postId;
    private Long authorId;
    private String title;
    private LocalDateTime timeSlot;
    private String publicContent;
    private PostStatus status;
    private Integer maxParticipants;
    private Gender genderLimit;
    private JobCategory jobCategoryLimit;
    private AgeLimit ageLimit;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private PostInfo(
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

    public static PostInfo toDto(
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
        return new PostInfo(
                id,
                authorId,
                title,
                timeSlot,
                publicContent,
                status,
                maxParticipants,
                genderLimit,
                jobCategoryLimit,
                ageLimit,
                createdAt,
                updatedAt
        );
    }
}
