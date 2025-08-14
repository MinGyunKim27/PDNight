package org.example.pdnight.domain.post.domain.post;

import jakarta.persistence.*;
import org.example.pdnight.global.common.entity.Timestamped;

@Entity
@Table(name = "outbox_event")
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
}
