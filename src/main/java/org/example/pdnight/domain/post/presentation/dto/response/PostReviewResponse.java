package org.example.pdnight.domain.post.presentation.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.pdnight.domain.post.domain.postReview.PostReviewDocument;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
public class PostReviewResponse {
    private String id;
    private Long postId;
    private Long authId;
    private String title;
    private String comment;
    private List<String> images;
    private List<String> videos;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    protected PostReviewResponse(String id, Long postId, Long authId, String title, String comment, List<String> images, List<String> videos, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.postId = postId;
        this.authId = authId;
        this.title = title;
        this.comment = comment;
        this.images = images;
        this.videos = videos;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static PostReviewResponse create(PostReviewDocument postReview) {
        return new PostReviewResponse(postReview.getId(), postReview.getPostId(), postReview.getAuthId(), postReview.getTitle(), postReview.getComment(), postReview.getImages(), postReview.getVideos(), postReview.getCreatedAt(), postReview.getUpdatedAt());
    }

}
