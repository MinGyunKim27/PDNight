package org.example.pdnight.domain.post.presentation.dto.response;

import lombok.Getter;
import org.example.pdnight.domain.post.domain.post.PostLike;

import java.time.LocalDateTime;

@Getter
public class PostLikeResponse {

    private Long postId;
    private Long userId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public PostLikeResponse(PostLike postLike) {
        this.postId = postLike.getPost().getId();
        this.userId = postLike.getUserId();
        this.createdAt = postLike.getCreatedAt();
        this.updatedAt = postLike.getUpdatedAt();
    }

    public static PostLikeResponse from(PostLike postLike) {
        return new PostLikeResponse(postLike);
    }

}