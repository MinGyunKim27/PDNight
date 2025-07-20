package org.example.pdnight.domain.review.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.pdnight.domain.common.entity.Timestamped;
import org.example.pdnight.domain.post.entity.Post;
import org.example.pdnight.domain.review.dto.request.ReviewRequestDto;
import org.example.pdnight.domain.user.entity.User;

import java.math.BigDecimal;

@Entity
@Table(
        name = "reviews",
        uniqueConstraints = @UniqueConstraint(columnNames = {"reviewer_id", "rated_user_id", "post_id"})
)
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Review extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewer_id")
    private User reviewer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rated_user_id")
    private User ratedUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @Column(precision = 2, scale = 1, nullable = false)
    private BigDecimal rate;

    @Column(length = 30)
    private String comment;

    public Review(User reviewer, User ratedUser, Post post, ReviewRequestDto dto) {
        this.reviewer = reviewer;
        this.ratedUser = ratedUser;
        this.post = post;
        this.rate = dto.getRate();
        this.comment = dto.getComment();
    }
}
