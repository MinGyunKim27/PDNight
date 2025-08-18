package org.example.pdnight.global.event;

import lombok.Getter;
import org.example.pdnight.domain.post.domain.post.InviteDocument;
import org.example.pdnight.domain.post.domain.post.PostDocument;
import org.example.pdnight.domain.post.domain.post.PostLikeDocument;
import org.example.pdnight.domain.post.domain.post.PostParticipantDocument;
import org.example.pdnight.domain.post.enums.AgeLimit;
import org.example.pdnight.domain.post.enums.Gender;
import org.example.pdnight.domain.post.enums.PostStatus;
import org.example.pdnight.global.common.enums.JobCategory;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class PostOutboxEvent {
    private Long id;
    private Long authorId;
    private String title;
    private LocalDateTime timeSlot;
    private String publicContent;
    private PostStatus status;
    private Integer maxParticipants;
    private Gender genderLimit;
    private JobCategory jobCategoryLimit;
    private AgeLimit ageLimit;
    private Boolean isFirstCome;
    private List<PostLikeDocument> postLikes;
    private List<PostParticipantDocument> postParticipants;
    private List<InviteDocument> invites;
    private List<String> tags;
    private Boolean isDeleted;
    private LocalDateTime deletedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public PostOutboxEvent(PostDocument postDocument){
        this.id = postDocument.getId();
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
        this.postLikes = postDocument.getPostLikes();
        this.postParticipants = postDocument.getPostParticipants();
        this.invites = postDocument.getInvites();
        this.tags = postDocument.getTags();
        this.isDeleted = postDocument.getIsDeleted();
        this.deletedAt = postDocument.getDeletedAt();
        this.createdAt = postDocument.getCreatedAt();
        this.updatedAt = postDocument.getUpdatedAt();
    }
}
