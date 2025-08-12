package org.example.pdnight.domain.promotion.domain;

import org.example.pdnight.domain.promotion.domain.entity.Promotion;

import java.util.Optional;

public interface PromotionCommander {

    Promotion save(Promotion promotion);

    void delete(Promotion promotion);

    Optional<Promotion> findById(Long id);
}
