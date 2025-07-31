package org.example.pdnight.domain.user.domain.reviewDomain;

import org.example.pdnight.domain.user.domain.entity.Review;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Component
public interface ReviewCommandQuery {
    Review save(Review review);
}
