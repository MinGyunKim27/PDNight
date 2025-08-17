package org.example.pdnight.domain.outbox.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.pdnight.domain.outbox.enums.OutboxStatus;
import org.example.pdnight.global.common.entity.Timestamped;

import java.time.LocalDateTime;

@Entity
@Table(name = "outbox_event")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OutboxEvent extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String aggregateType;
    private String aggregateId;
    private String type;

    @Lob    // Large Object. JSON 직렬화 결과여서 갈이가 길 수 있음
    private String payload;

    @Enumerated(EnumType.STRING)
    private OutboxStatus status;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();

    public void setStatus(OutboxStatus status) {
        this.status = status;
    }

    private OutboxEvent(String aggregateType, Long aggregateId, String type, String payload, OutboxStatus status) {
        this.aggregateType = aggregateType;
        this.aggregateId = aggregateId.toString();
        this.type = type;
        this.payload = payload;
        this.status = status;
    }

    public static OutboxEvent create(String aggregateType, Long aggregateId, String type, String payload, OutboxStatus status) {
        return new OutboxEvent(aggregateType, aggregateId, type, payload, status);
    }

    public void markAsSent() {
        this.status = OutboxStatus.SENT;
        this.updatedAt = LocalDateTime.now();
    }

    public void markAsFailed() {
        this.status = OutboxStatus.FAILED;
        this.updatedAt = LocalDateTime.now();
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
