package org.example.pdnight.domain.promotion.domain;

import org.example.pdnight.domain.promotion.domain.entity.Promotion;
import org.example.pdnight.domain.promotion.domain.entity.PromotionParticipant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;


public interface PromotionReader {

    Page<Promotion> findAllPromotion(Pageable pageable);


    Page<PromotionParticipant> findByPromotionWithUser(Promotion promotion, Pageable pageable);

    Page<Promotion> getMyParticipantPromotions(Long userId, Pageable pageable);

    Optional<Promotion> findById(Long id);
}
