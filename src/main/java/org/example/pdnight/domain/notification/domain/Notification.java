package org.example.pdnight.domain.notification.domain;

import jakarta.persistence.*;
import lombok.*;
import org.example.pdnight.domain.notification.enums.NotificationType;
import org.example.pdnight.global.common.entity.Timestamped;

@Entity
@Table(name = "notifications")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 알림을 받을 유저
    private Long receiver;

    // 알림 내용
    @Column(nullable = false)
    private String content;

    // 알림을 발생시킨 유저
    private Long sender;

    // 알림 타입 (댓글, 초대, 수락, 팔로우 등)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    // 읽음 여부
    @Column(nullable = false)
    private Boolean isRead = false;

    public void markAsRead() {
        this.isRead = true;
    }

    private Notification(
            Long receiver,
            String content,
            Long sender,
            NotificationType type
    ) {
        this.receiver = receiver;
        this.content = content;
        this.sender = sender;
        this.type = type;
    }

    public static Notification from(
            Long receiver,
            String content,
            Long sender,
            NotificationType type
    ) {
        return new Notification(
                receiver,
                content,
                sender,
                type
        );
    }

}
