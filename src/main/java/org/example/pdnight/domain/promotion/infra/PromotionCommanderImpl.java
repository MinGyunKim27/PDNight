package org.example.pdnight.domain.promotion.infra;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.promotion.domain.PromotionCommander;
import org.example.pdnight.domain.promotion.domain.entity.Promotion;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PromotionCommanderImpl implements PromotionCommander {

    private final PromotionJpaRepository promotionJpaRepository;

    @Override
    public Promotion save(Promotion promotion) {
        return promotionJpaRepository.save(promotion);
    }

    @Override
    public void delete(Promotion promotion) {
        promotionJpaRepository.delete(promotion);
    }

    @Override
    public Optional<Promotion> findById(Long id) {
        return promotionJpaRepository.findById(id);
    }
}
