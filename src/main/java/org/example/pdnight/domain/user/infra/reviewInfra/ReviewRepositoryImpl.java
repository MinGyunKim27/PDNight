package org.example.pdnight.domain.user.infra.reviewInfra;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.user.domain.entity.Review;
import org.example.pdnight.domain.user.domain.reviewDomain.ReviewCommandQuery;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
public class ReviewRepositoryImpl implements ReviewCommandQuery {

    private final ReviewJpaRepository reviewJpaRepository;

    @Override
    public Review save(Review review) {
        return reviewJpaRepository.save(review);
    }

}
