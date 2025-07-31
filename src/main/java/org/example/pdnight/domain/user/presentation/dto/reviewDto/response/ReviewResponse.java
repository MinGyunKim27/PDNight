package org.example.pdnight.domain.user.presentation.dto.reviewDto.response;

import lombok.Getter;
import org.example.pdnight.domain.user.domain.entity.Review;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
public class ReviewResponse {
    private Long id;
    private Long reviewerId;
    private Long revieweeId;
    private Long postId;
    private BigDecimal rate;
    private String comment;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ReviewResponse(Review review) {
        this.id = review.getId();
        this.postId = review.getPostId();
        this.reviewerId = review.getReviewerId();
        this.revieweeId = review.getRevieweeId();
        this.rate = review.getRate();
        this.comment = review.getComment();
        this.createdAt = review.getCreatedAt();
        this.updatedAt = review.getUpdatedAt();
    }

    public static ReviewResponse from(Review review) {
        return new ReviewResponse(review);
    }

}
