package org.example.pdnight.domain.user.infra.reviewInfra;

import org.example.pdnight.domain.user.domain.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewJpaRepository extends JpaRepository<Review, Long> {
}
