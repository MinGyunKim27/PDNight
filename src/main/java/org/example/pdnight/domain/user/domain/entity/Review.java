package org.example.pdnight.domain.user.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.pdnight.domain.common.entity.Timestamped;
import org.example.pdnight.domain.post.entity.Post;

import java.math.BigDecimal;

@Entity
@Table(
        name = "reviews",
        uniqueConstraints = @UniqueConstraint(columnNames = {"reviewer_id", "rated_user_id", "post_id"})
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long reviewerId;

    @Column(nullable = false)
    private Long ratedUserId;
    
    private Long postId;

    @Column(precision = 2, scale = 1, nullable = false)
    private BigDecimal rate;

    @Column(length = 30)
    private String comment;

    private Review(Long reviewerId, Long ratedUserId, Long postId, BigDecimal rate, String comment) {
        this.reviewerId = reviewerId;
        this.ratedUserId = ratedUserId;
        this.postId = postId;
        this.rate = rate;
        this.comment = comment;
    }

    public static Review create(Long reviewerId, Long ratedUserId, Long postId, BigDecimal rate, String comment) {
        return new Review(reviewerId, ratedUserId, postId, rate, comment);
    }

    public void removePost() {
        this.postId = null;
    }
}
