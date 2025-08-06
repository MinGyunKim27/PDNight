package org.example.pdnight.domain.promotion.domain;

import org.example.pdnight.domain.promotion.domain.entity.Promotion;

public interface PromotionCommander {

    Promotion save(Promotion promotion);

    void delete(Promotion promotion);
}
