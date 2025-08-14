package org.example.pdnight.domain.post.domain.post;

import org.springframework.data.annotation.Id;
import lombok.Getter;
import org.example.pdnight.domain.post.enums.AgeLimit;
import org.example.pdnight.domain.post.enums.Gender;
import org.example.pdnight.domain.post.enums.PostStatus;
import org.example.pdnight.global.common.enums.JobCategory;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(indexName = "posts")
@Getter
public class PostDocument {

    @Id
    private final Long id;

    private final Long authorId;

    private final String title;

    @Field(type = FieldType.Date, format = {}, pattern = "yyyy-MM-dd'T'HH:mm:ss||strict_date_time")
    private final LocalDateTime timeSlot;
    private final String publicContent;

    @Field(type = FieldType.Keyword)
    private final PostStatus status;

    private final Integer maxParticipants;

    @Field(type = FieldType.Keyword)
    private final Gender genderLimit;

    @Field(type = FieldType.Keyword)
    private final JobCategory jobCategoryLimit;

    @Field(type = FieldType.Keyword)
    private final AgeLimit ageLimit;

    private final Boolean isFirstCome;

    // Object or Nested
    @Field(type = FieldType.Nested)
    private final List<PostLikeDocument> postLikes;

    // 참여자 리스트 전체를 가져와서 서버에서 구분하는 것
    @Field(type = FieldType.Nested)
    private final List<PostParticipantDocument> postParticipants;

    @Field(type = FieldType.Nested)
    private final List<InviteDocument> invites;

    @Field(type = FieldType.Keyword)
    private final List<String> tags;

    private final Boolean isDeleted;

    @Field(type = FieldType.Date, format = {}, pattern = "yyyy-MM-dd'T'HH:mm:ss||strict_date_time")
    private final LocalDateTime deletedAt;

    @Field(type = FieldType.Date, format = {}, pattern = "yyyy-MM-dd'T'HH:mm:ss||strict_date_time")
    private final LocalDateTime createdAt;

    @Field(type = FieldType.Date, format = {}, pattern = "yyyy-MM-dd'T'HH:mm:ss||strict_date_time")
    private LocalDateTime updatedAt;

    private PostDocument(
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
        this.timeSlot = timeSlot;
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
        this.deletedAt = deletedAt;
        this.createdAt = createdAt;
        this.updatedAt = LocalDateTime.now();
    }

    public static PostDocument createPostDocument(
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
        return new PostDocument(
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
                isFirstCome,
                postLikes,
                postParticipants,
                invites,
                (tags != null) ? tags : new ArrayList<>(),
                isDeleted,
                deletedAt,
                createdAt
        );
    }

}
