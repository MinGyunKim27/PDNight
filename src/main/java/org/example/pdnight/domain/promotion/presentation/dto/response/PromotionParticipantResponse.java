package org.example.pdnight.domain.promotion.presentation.dto.response;

import lombok.Getter;
import org.example.pdnight.domain.promotion.domain.entity.PromotionParticipant;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
public class PromotionParticipantResponse {
    private Long id;
    private Long promotionId;
    private List<Long> user = new ArrayList<>();
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private PromotionParticipantResponse(PromotionParticipant promotionParticipant) {
        this.id = promotionParticipant.getId();
        this.promotionId = promotionParticipant.getPromotion().getId();
        this.user.add(promotionParticipant.getUserId());
        this.createdAt = promotionParticipant.getCreatedAt();
        this.updatedAt = promotionParticipant.getUpdatedAt();
    }

    public static PromotionParticipantResponse from(PromotionParticipant promotionParticipant) {
        return new PromotionParticipantResponse(promotionParticipant);
    }
}
