package org.example.pdnight.domain1.review.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.pdnight.domain1.common.entity.Timestamped;
import org.example.pdnight.domain1.post.entity.Post;
import org.example.pdnight.domain1.user.entity.User;

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

    private Review(User reviewer, User ratedUser, Post post, BigDecimal rate, String comment) {
        this.reviewer = reviewer;
        this.ratedUser = ratedUser;
        this.post = post;
        this.rate = rate;
        this.comment = comment;
    }

    public static Review create(User reviewer, User ratedUser, Post post, BigDecimal rate, String comment) {
        return new Review(reviewer, ratedUser, post, rate, comment);
    }

    public void removePost() {
        this.post = null;
    }
}
