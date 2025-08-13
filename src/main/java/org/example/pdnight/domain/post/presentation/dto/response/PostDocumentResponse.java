package org.example.pdnight.domain.post.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import org.example.pdnight.domain.post.domain.post.PostDocument;
import org.example.pdnight.domain.post.enums.AgeLimit;
import org.example.pdnight.domain.post.enums.Gender;
import org.example.pdnight.domain.post.enums.JoinStatus;
import org.example.pdnight.domain.post.enums.PostStatus;
import org.example.pdnight.global.common.enums.JobCategory;

import java.time.LocalDateTime;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostDocumentResponse {

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
    private final boolean isFirstCome;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    private PostDocumentResponse(PostDocument postDocument) {
        this.postId = postDocument.getId();
        this.authorId = postDocument.getAuthorId();
        this.title = postDocument.getTitle();
        this.timeSlot = postDocument.getTimeSlot();
        this.publicContent = postDocument.getPublicContent();
        this.status = postDocument.getStatus();
        this.maxParticipants = postDocument.getMaxParticipants();
        this.genderLimit = postDocument.getGenderLimit();
        this.jobCategoryLimit = postDocument.getJobCategoryLimit();
        this.ageLimit = postDocument.getAgeLimit();
        this.isFirstCome = postDocument.getIsFirstCome();
        this.createdAt = postDocument.getCreatedAt();
        this.updatedAt = postDocument.getUpdatedAt();
    }

    private PostDocumentResponse(PostDocument postDocument, Integer acceptedParticipantsCount, Integer participantsCount) {
        this.postId = postDocument.getId();
        this.authorId = postDocument.getAuthorId();
        this.title = postDocument.getTitle();
        this.timeSlot = postDocument.getTimeSlot();
        this.publicContent = postDocument.getPublicContent();
        this.status = postDocument.getStatus();
        this.maxParticipants = postDocument.getMaxParticipants();
        this.genderLimit = postDocument.getGenderLimit();
        this.jobCategoryLimit = postDocument.getJobCategoryLimit();
        this.ageLimit = postDocument.getAgeLimit();
        this.isFirstCome = postDocument.getIsFirstCome();
        this.acceptedParticipantsCount = acceptedParticipantsCount;
        this.participantsCount = participantsCount;
        this.createdAt = postDocument.getCreatedAt();
        this.updatedAt = postDocument.getUpdatedAt();
    }

    public static PostDocumentResponse toDto(PostDocument postDocument) {
        return new PostDocumentResponse(postDocument);
    }

    public static PostDocumentResponse toDtoWithCount(PostDocument postDocument, int acceptedParticipantsCount, int participantsCount) {
        return new PostDocumentResponse(postDocument, acceptedParticipantsCount, participantsCount);
    }

}
