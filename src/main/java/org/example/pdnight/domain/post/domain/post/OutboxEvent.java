package org.example.pdnight.domain.post.domain.post;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.pdnight.domain.post.enums.OutboxStatus;
import org.example.pdnight.global.common.entity.Timestamped;

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

    public void setStatus(OutboxStatus status) {
        this.status = status;
    }
}
