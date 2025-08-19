package org.example.pdnight.domain.post.presentation.dto.response;

import lombok.Getter;
import org.example.pdnight.domain.post.enums.AgeLimit;
import org.example.pdnight.domain.post.enums.Gender;
import org.example.pdnight.domain.post.enums.PostStatus;
import org.example.pdnight.global.common.enums.JobCategory;

import java.time.LocalDateTime;
import java.util.List;

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
    private List<String> tagList;
    private AgeLimit ageLimit;
    private boolean isFirstCome;
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
            List<String> tagList,
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
        this.tagList = tagList;
        this.ageLimit = ageLimit;
        this.isFirstCome = isFirstCome;
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
            List<String> tagList,
            AgeLimit ageLimit,
            boolean isFirstCome,
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
                tagList,
                ageLimit,
                isFirstCome,
                createdAt,
                updatedAt
        );
    }
}
