package org.example.pdnight.domain.promotion.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.pdnight.global.common.entity.Timestamped;

@Entity
@Table(name = "promotion_participants")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PromotionParticipant extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "promotion_id")
    private Promotion promotion;

    @Column(nullable = false)
    private Long userId;

    private PromotionParticipant(Promotion promotion, Long userId) {
        this.promotion = promotion;
        this.userId = userId;
    }

    public static PromotionParticipant create(Promotion promotion, Long userId) {
        return new PromotionParticipant(promotion, userId);
    }

}
