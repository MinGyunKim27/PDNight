package org.example.pdnight.domain.post.domain.postReview;

import jakarta.persistence.Id;
import lombok.Getter;
import org.example.pdnight.domain.post.presentation.dto.request.PostReviewRequest;
import org.example.pdnight.domain.post.presentation.dto.request.PostReviewUpdateRequest;
import org.springframework.data.elasticsearch.annotations.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(indexName = "post_reviews")
@Getter
public class PostReviewDocument {
    @Id
    private String id;

    private Long postId;
    private Long authId;
    private String title;
    private String comment;
    private List<String> images;
    private List<String> videos;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    protected PostReviewDocument(Long postId, Long authId, String title, String comment, List<String> images, List<String> videos, LocalDateTime createdAt) {
        this.postId = postId;
        this.authId = authId;
        this.title = title;
        this.comment = comment;
        this.images = images;
        this.videos = videos;
        this.createdAt = createdAt;
        this.updatedAt = LocalDateTime.now();
    }

    public static PostReviewDocument create(PostReviewRequest request, Long userId, List<String> imagesPath, List<String> videosPath, LocalDateTime createdAt) {
        return new PostReviewDocument(Long.parseLong(request.getPostId()), userId, request.getTitle(), request.getComment(), imagesPath, videosPath, createdAt);
    }

    public void update(PostReviewUpdateRequest request , List<String> imagesPath, List<String> videosPath) {
        if (request.getTitle() != null) {
            this.title = request.getTitle();
        }
        if (request.getComment() != null) {
            this.comment = request.getComment();
        }
        if (request.getImages() != null) {
            this.images = imagesPath;
        }
        if (request.getVideos() != null) {
            this.videos = videosPath;
        }
        this.updatedAt = LocalDateTime.now();
    }
}
