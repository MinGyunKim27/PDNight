package org.example.pdnight.domain.post.domain.post;

import lombok.Getter;
import org.example.pdnight.domain.post.enums.JoinStatus;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "post_participants")
@Getter
public class PostParticipantDocument {

    private Long postId;

    private Long userId;

    @Field(type = FieldType.Keyword)
    private JoinStatus status;

    private PostParticipantDocument(Long postId, Long userId, JoinStatus status) {
        this.postId = postId;
        this.userId = userId;
        this.status = status;
    }

    public static PostParticipantDocument create(Long postId, Long userId, JoinStatus status) {
        return new PostParticipantDocument(postId, userId, status);
    }

}