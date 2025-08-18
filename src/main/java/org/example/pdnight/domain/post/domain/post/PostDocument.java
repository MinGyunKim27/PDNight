package org.example.pdnight.domain.post.domain.post;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;

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
import java.util.Map;

@Document(indexName = "posts")
@Getter
@NoArgsConstructor(force = true)
@Builder
@AllArgsConstructor
public class PostDocument {

    @Id
    private final Long id;

    private final Long authorId;

    private final String title;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
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

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @Field(type = FieldType.Date, format = {}, pattern = "yyyy-MM-dd'T'HH:mm:ss||strict_date_time")
    private final LocalDateTime deletedAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @Field(type = FieldType.Date, format = {}, pattern = "yyyy-MM-dd'T'HH:mm:ss||strict_date_time")
    private final LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @Field(type = FieldType.Date, format = {}, pattern = "yyyy-MM-dd'T'HH:mm:ss||strict_date_time")
    private LocalDateTime updatedAt;
//    @JsonCreator
//    private PostDocument(
//            @JsonProperty("id") Long id,
//            @JsonProperty("authorId") Long authorId,
//            @JsonProperty("title") String title,
//            @JsonProperty("timeSlot") LocalDateTime timeSlot,
//            @JsonProperty("publicContent") String publicContent,
//            @JsonProperty("status") PostStatus status,
//            @JsonProperty("maxParticipants") Integer maxParticipants,
//            @JsonProperty("genderLimit") Gender genderLimit,
//            @JsonProperty("jobCategoryLimit") JobCategory jobCategoryLimit,
//            @JsonProperty("ageLimit") AgeLimit ageLimit,
//            @JsonProperty("isFirstCome") Boolean isFirstCome,
//            @JsonProperty("postLikes") List<PostLikeDocument> postLikes,
//            @JsonProperty("postParticipants") List<PostParticipantDocument> postParticipants,
//            @JsonProperty("invites") List<InviteDocument> invites,
//            @JsonProperty("tags") List<String> tags,
//            @JsonProperty("isDeleted") Boolean isDeleted,
//            @JsonProperty("deletedAt") LocalDateTime deletedAt,
//            @JsonProperty("createdAt") LocalDateTime createdAt
//    ) {
//
//        this.id = id;
//        this.authorId = authorId;
//        this.title = title;
//        this.timeSlot = timeSlot;
//        this.publicContent = publicContent;
//        this.status = status;
//        this.maxParticipants = maxParticipants;
//        this.genderLimit = genderLimit;
//        this.jobCategoryLimit = jobCategoryLimit;
//        this.ageLimit = ageLimit;
//        this.isFirstCome = isFirstCome;
//        this.postLikes = postLikes;
//        this.postParticipants = postParticipants;
//        this.tags = (tags != null) ? tags : new ArrayList<>();
//        this.invites = invites;
//        this.isDeleted = isDeleted;
//        this.deletedAt = deletedAt;
//        this.createdAt = createdAt;
//        this.updatedAt = LocalDateTime.now();
//    }
//
//    public static PostDocument createPostDocument(
//            Long id,
//            Long authorId,
//            String title,
//            LocalDateTime timeSlot,
//            String publicContent,
//            PostStatus status,
//            Integer maxParticipants,
//            Gender genderLimit,
//            JobCategory jobCategoryLimit,
//            AgeLimit ageLimit,
//            Boolean isFirstCome,
//            List<PostLikeDocument> postLikes,
//            List<PostParticipantDocument> postParticipants,
//            List<InviteDocument> invites,
//            List<String> tags,
//            Boolean isDeleted,
//            LocalDateTime deletedAt,
//            LocalDateTime createdAt
//    ) {
//        return new PostDocument(
//                id,
//                authorId,
//                title,
//                timeSlot,
//                publicContent,
//                status,
//                maxParticipants,
//                genderLimit,
//                jobCategoryLimit,
//                ageLimit,
//                isFirstCome,
//                postLikes,
//                postParticipants,
//                invites,
//                (tags != null) ? tags : new ArrayList<>(),
//                isDeleted,
//                deletedAt,
//                createdAt
//        );
//    }


    // ====== 새로 추가한 카운터/버킷 필드 ======
    @Field(type = FieldType.Long)
    private Long viewCount;

    @Field(type = FieldType.Long)
    private Long applyCount;

    @Field(type = FieldType.Long)
    private Long popView7d;

    @Field(type = FieldType.Long)
    private Long popApply7d;

    @Field(type = FieldType.Object)
    private Map<String, Integer> viewDaily;   // key: "yyyy-MM-dd"

    @Field(type = FieldType.Object)
    private Map<String, Integer> applyDaily;  // key: "yyyy-MM-dd"
    // =========================================

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
            LocalDateTime createdAt,
            LocalDateTime updatedAt
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
        this.postLikes = postLikes != null ? postLikes : new ArrayList<>();
        this.postParticipants = postParticipants != null ? postParticipants : new ArrayList<>();
        this.invites = invites != null ? invites : new ArrayList<>();
        this.tags = tags != null ? tags : new ArrayList<>();
        this.isDeleted = isDeleted;
        this.deletedAt = deletedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Builder 사용 시 updatedAt 자동 설정
    public void touchUpdatedAt() {
        this.updatedAt = LocalDateTime.now();
    }
}
