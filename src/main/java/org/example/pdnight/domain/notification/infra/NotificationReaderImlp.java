package org.example.pdnight.domain.notification.infra;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.notification.domain.Notification;
import org.example.pdnight.domain.notification.domain.NotificationReader;
import org.example.pdnight.domain.notification.domain.QNotification;
import org.example.pdnight.global.common.exception.BaseException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class NotificationReaderImlp implements NotificationReader {
    private final JPAQueryFactory queryFactory;

    public Notification findByIdIsReadFalse(Long id){
        QNotification notification = QNotification.notification;

        Optional<Notification> result = queryFactory.selectFrom(notification)
                .where(notification.id.eq(id).and(notification.isRead.eq(false)))
                .stream().findFirst();
        return result
                .orElseThrow(() -> new BaseException(HttpStatus.NOT_FOUND,"알림을 찾을 수 없습니다!"));
    }

    public Notification findById(Long id){
        QNotification notification = QNotification.notification;

        Optional<Notification> result = queryFactory.selectFrom(notification)
                .where(notification.id.eq(id))
                .stream().findFirst();
        return result
                .orElseThrow(() -> new BaseException(HttpStatus.NOT_FOUND,"알림을 찾을 수 없습니다!"));
    }
}
