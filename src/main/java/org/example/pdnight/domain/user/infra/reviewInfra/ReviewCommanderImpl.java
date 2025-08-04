package org.example.pdnight.domain.user.infra.reviewInfra;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.user.domain.entity.Review;
import org.example.pdnight.domain.user.domain.reviewDomain.ReviewCommander;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ReviewCommanderImpl implements ReviewCommander {

    private final ReviewJpaRepository reviewJpaRepository;

    @Override
    public Review save(Review review) {
        return reviewJpaRepository.save(review);
    }

}
