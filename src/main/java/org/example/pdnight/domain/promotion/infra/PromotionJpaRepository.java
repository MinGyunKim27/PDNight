package org.example.pdnight.domain.promotion.infra;

import org.example.pdnight.domain.promotion.domain.entity.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PromotionJpaRepository extends JpaRepository<Promotion, Long>{

}
