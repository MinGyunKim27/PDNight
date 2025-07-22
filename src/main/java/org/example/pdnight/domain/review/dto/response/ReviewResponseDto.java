package org.example.pdnight.domain.review.dto.response;

import lombok.Getter;
import org.example.pdnight.domain.review.entity.Review;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
public class ReviewResponseDto {
    private Long id;
    private Long postId;
    private Long ratedUserId;
    private BigDecimal rate;
    private String comment;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ReviewResponseDto(Review review) {
        this.id = review.getId();
        this.postId = review.getPost().getId();
        this.ratedUserId = review.getRatedUser().getId();
        this.rate = review.getRate();
        this.comment = review.getComment();
        this.createdAt = review.getCreatedAt();
        this.updatedAt = review.getUpdatedAt();
    }

}
