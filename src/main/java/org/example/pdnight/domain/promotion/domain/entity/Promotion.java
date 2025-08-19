package org.example.pdnight.domain.promotion.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.pdnight.global.common.entity.Timestamped;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.CascadeType.ALL;

@Entity
@Table(name = "promotions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Promotion extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @OneToMany(mappedBy = "promotion", cascade = ALL, orphanRemoval = true)
    private List<PromotionParticipant> promotionParticipants = new ArrayList<>();

    @Column(nullable = false)
    private Integer maxParticipants;

    @Column(nullable = false)
    private LocalDateTime promotionStartDate;

    @Column(nullable = false)
    private LocalDateTime promotionEndDate;

    protected Promotion(String title, String content, Integer maxParticipants, LocalDateTime promotionStartDate, LocalDateTime promotionEndDate) {
        this.title = title;
        this.content = content;
        this.maxParticipants = maxParticipants;
        this.promotionStartDate = promotionStartDate;
        this.promotionEndDate = promotionEndDate;
    }

    private Promotion(Long promotionId, int i) {
        this.id = promotionId;
        this.maxParticipants = i;
    }

    private Promotion(Long id, String title, int maxParticipants) {
        this.id = id;
        this.title = title;
        this.maxParticipants = maxParticipants;
    }

    public static Promotion from(Long promotionId, int id) {
        return new Promotion(promotionId, id);
    }

    public static Promotion from(String title, String content, Integer maxParticipants, LocalDateTime promotionStartDate, LocalDateTime promotionEndDate) {
        return new Promotion(title, content, maxParticipants, promotionStartDate, promotionEndDate);
    }

    public static Promotion from(Long id, String title, int maxParticipants) {
        return new Promotion(id, title, maxParticipants);
    }

    public void updatePromotion(String title, String content, Integer maxParticipants, LocalDateTime promotionStartDate, LocalDateTime promotionEndDate) {
        if (title != null) this.title = title;
        if (content != null) this.content = content;
        if (maxParticipants != null && maxParticipants >= 1) this.maxParticipants = maxParticipants;
        if (promotionStartDate != null) this.promotionStartDate = promotionStartDate;
        if (promotionEndDate != null) this.promotionEndDate = promotionEndDate;
    }

    public void addParticipant(PromotionParticipant promotionParticipant) {
        this.promotionParticipants.add(promotionParticipant);
    }
}
