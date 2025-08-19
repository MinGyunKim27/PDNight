package org.example.pdnight.domain.notification.infra;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.notification.domain.Notification;
import org.example.pdnight.domain.notification.domain.NotificationReader;
import org.example.pdnight.domain.notification.domain.QNotification;
import org.example.pdnight.global.common.exception.BaseException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class NotificationReaderImpl implements NotificationReader {
    private final JPAQueryFactory queryFactory;

    @Override
    public Notification findById(Long id) {
        QNotification notification = QNotification.notification;

        Optional<Notification> result = queryFactory.selectFrom(notification)
                .where(notification.id.eq(id))
                .stream().findFirst();
        return result
                .orElseThrow(() -> new BaseException(HttpStatus.NOT_FOUND, "알림을 찾을 수 없습니다!"));
    }

    @Override
    public Page<Notification> findAll(Long userId, Pageable pageable) {
        QNotification notification = QNotification.notification;

        List<Notification> result = queryFactory
                .select(notification)
                .from(notification)
                .where(notification.receiverId.eq(userId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(notification.count())
                .from(notification).
                where(notification.receiverId.eq(userId));

        return PageableExecutionUtils.getPage(result, pageable, countQuery::fetchOne);
    }

    @Override
    public Page<Notification> findIsReadFalse(Long userId, Pageable pageable) {
        QNotification notification = QNotification.notification;

        List<Notification> result = queryFactory
                .select(notification)
                .from(notification)
                .where(notification.receiverId.eq(userId)
                        .and(notification.isRead.eq(false)))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(notification.count())
                .from(notification).
                where(notification.receiverId.eq(userId)
                        .and(notification.isRead.eq(false)));

        return PageableExecutionUtils.getPage(result, pageable, countQuery::fetchOne);
    }
}
