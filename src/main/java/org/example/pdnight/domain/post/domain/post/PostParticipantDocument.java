package org.example.pdnight.domain.post.domain.post;

import lombok.Getter;
import org.example.pdnight.domain.post.enums.JoinStatus;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;

@Document(indexName = "post_participants")
@Getter
public class PostParticipantDocument {

    private Long postId;

    private Long userId;

    @Field(type = FieldType.Keyword)
    private JoinStatus status;

    @Field(type = FieldType.Date, format = {}, pattern = "yyyy-MM-dd'T'HH:mm:ss||strict_date_time")
    private LocalDateTime createdAt;

    @Field(type = FieldType.Date, format = {}, pattern = "yyyy-MM-dd'T'HH:mm:ss||strict_date_time")
    private LocalDateTime updatedAt;

    private PostParticipantDocument(Long postId, Long userId, JoinStatus status, LocalDateTime createdAt) {
        this.postId = postId;
        this.userId = userId;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = LocalDateTime.now();
    }

    public static PostParticipantDocument create(Long postId, Long userId, JoinStatus status, LocalDateTime createdAt) {
        return new PostParticipantDocument(postId, userId, status, createdAt);
    }

}