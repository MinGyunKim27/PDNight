package org.example.pdnight.domain.post.domain.post;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import org.example.pdnight.domain.post.enums.AgeLimit;
import org.example.pdnight.domain.post.enums.Gender;
import org.example.pdnight.domain.post.enums.PostStatus;
import org.example.pdnight.global.common.enums.JobCategory;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Getter
public class PostDocumentDto {
    private final Long id;

    private final Long authorId;

    private final String title;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private final String timeSlot;
    private final String publicContent;

    private final PostStatus status;

    private final Integer maxParticipants;
    private final Gender genderLimit;

    private final JobCategory jobCategoryLimit;

    private final AgeLimit ageLimit;

    private final Boolean isFirstCome;
    private final List<PostLikeDocument> postLikes;

    private final List<PostParticipantDocument> postParticipants;

    private final List<InviteDocument> invites;

    private final List<String> tags;

    private final Boolean isDeleted;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private final String deletedAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private final String createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private String updatedAt;

    private PostDocumentDto(
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
            Boolean isFirstCome,
            List<PostLikeDocument> postLikes,
            List<PostParticipantDocument> postParticipants,
            List<InviteDocument> invites,
            List<String> tags,
            Boolean isDeleted,
            LocalDateTime deletedAt,
            LocalDateTime createdAt
    ) {

        this.id = id;
        this.authorId = authorId;
        this.title = title;
        this.timeSlot = timeSlot
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
        this.publicContent = publicContent;
        this.status = status;
        this.maxParticipants = maxParticipants;
        this.genderLimit = genderLimit;
        this.jobCategoryLimit = jobCategoryLimit;
        this.ageLimit = ageLimit;
        this.isFirstCome = isFirstCome;
        this.postLikes = postLikes;
        this.postParticipants = postParticipants;
        this.tags = (tags != null) ? tags : new ArrayList<>();
        this.invites = invites;
        this.isDeleted = isDeleted;
        this.deletedAt = deletedAt != null ? deletedAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")) : null;
        this.createdAt = createdAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
        this.updatedAt = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
    }

    public static PostDocumentDto from(PostDocument postDocument) {
        return new PostDocumentDto(
                postDocument.getId(),
                postDocument.getAuthorId(),
                postDocument.getTitle(),
                postDocument.getTimeSlot(),
                postDocument.getPublicContent(),
                postDocument.getStatus(),
                postDocument.getMaxParticipants(),
                postDocument.getGenderLimit(),
                postDocument.getJobCategoryLimit(),
                postDocument.getAgeLimit(),
                postDocument.getIsFirstCome(),
                postDocument.getPostLikes(),
                postDocument.getPostParticipants(),
                postDocument.getInvites(),
                (postDocument.getTags() != null) ? postDocument.getTags() : new ArrayList<>(),
                postDocument.getIsDeleted(),
                postDocument.getDeletedAt(),
                postDocument.getCreatedAt()
        );
    }

    public Long getId() {
        return this.id;
    }
}
