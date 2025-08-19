package org.example.pdnight.domain.promotion.presentation.dto.response;

import lombok.Getter;
import org.example.pdnight.domain.promotion.domain.entity.Promotion;

import java.time.LocalDateTime;

@Getter
public class PromotionResponse {
    private Long id;
    private String title;
    private String content;
    private Integer maxParticipants;
    private LocalDateTime promotionStartDate;
    private LocalDateTime promotionEndDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    protected PromotionResponse(Promotion promotion) {
        this.id = promotion.getId();
        this.title = promotion.getTitle();
        this.content = promotion.getContent();
        this.maxParticipants = promotion.getMaxParticipants();
        this.promotionStartDate = promotion.getPromotionStartDate();
        this.promotionEndDate = promotion.getPromotionEndDate();
        this.createdAt = promotion.getCreatedAt();
        this.updatedAt = promotion.getUpdatedAt();
    }

    public static PromotionResponse from(Promotion promotion) {
        return new PromotionResponse(promotion);
    }
}
