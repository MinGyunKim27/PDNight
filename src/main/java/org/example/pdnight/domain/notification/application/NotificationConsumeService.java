package org.example.pdnight.domain.notification.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.pdnight.domain.notification.domain.Notification;
import org.example.pdnight.domain.notification.domain.NotificationCommander;
import org.example.pdnight.domain.notification.domain.NotificationReader;
import org.example.pdnight.domain.notification.enums.NotificationType;
import org.example.pdnight.domain.notification.infra.NotificationSocketSender;
import org.example.pdnight.domain.notification.presentation.dto.NotificationResponse;
import org.example.pdnight.domain.notification.presentation.dto.event.PostConfirmedEvent;
import org.example.pdnight.global.common.exception.BaseException;
import org.example.pdnight.global.event.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationConsumeService {
    private final NotificationReader notificationReader;
    private final NotificationCommander notificationCommander;
    private final NotificationSocketSender webSocketSender;

    public void isReadCheck(Long id, Long userid) {
        Notification notification = notificationReader.findByIdIsReadFalse(id);
        if (userid.equals(notification.getReceiver())) {
            notification.markAsRead();
        } else {
            throw new BaseException(HttpStatus.BAD_REQUEST, "본인이 아닙니다!");
        }
        notificationCommander.save(notification);
    }

    public void handlePostConfirmed(PostConfirmedEvent event) {
        // 참가자들에게 전송
        for (Long userId : event.confirmedUserIds()) {
            Notification notification = Notification.from(null, userId, "신청하신 모임이 확정되었습니다!", event.authorId(), NotificationType.POST_CONFIRMED);
            NotificationResponse dto = NotificationResponse.from(notification);
            notificationCommander.save(notification);
            log.info("모임 확정 알림 저장 완료 userId : {}", userId);
            webSocketSender.sendToUser(userId, dto);
        }

        // 작성자에게도 전송
        Notification notification = Notification.from(null, event.authorId(), "생성하신 모임이 확정되었습니다!", event.authorId(), NotificationType.POST_CONFIRMED);
        NotificationResponse dto = NotificationResponse.from(notification);
        notificationCommander.save(notification);
        log.info("모임 확정 알림 저장 완료 userId : {}", event.authorId());
        webSocketSender.sendToUser(event.authorId(), dto);
    }

    // 모임 참가 신청 알림
    public void handlePostApplied(PostParticipateAppliedEvent event) {

        // 작성자에게 전송
        Notification notification = Notification.from(null, event.authorId(), "사용자가 모임에 신청했습니다!", event.authorId(), NotificationType.PARTICIPANT_APPLIED);
        NotificationResponse dto = NotificationResponse.from(notification);
        notificationCommander.save(notification);
        log.info("모임 신청 알림 저장 완료!");
        webSocketSender.sendToUser(event.authorId(), dto);
    }

    // 팔로잉 중인 사람이 게시글 생성
    public void handleFolloweePostCreated(FolloweePostCreatedEvent event) {
        // 나를 팔로우 중인 사람들에게 전송...?
        Notification notification = Notification.from(event.postId(), event.authorId(), "팔로우 중인 유저가 게시물을 생성했습니다!", event.authorId(), NotificationType.FOLLOWEE_POST_CREATED);
        NotificationResponse dto = NotificationResponse.from(notification);
        notificationCommander.save(notification);
        log.info("팔로우 게시물 생성 알림 저장 완료!");
        webSocketSender.sendToUser(event.authorId(), dto);
    }

    // 모임 참여 수락
    public void handlePostApplyAccepted(PostApplyAcceptedEvent event) {
    }

    // 모임 참여 거절
    public void handlePostApplyDeniedEvent(PostApplyDeniedEvent event) {
    }

    // 초대 전송
    public void handleInviteSent(InviteSentEvent event) {

    }

    // 초대 수락
    public void handleInviteAcceptedEvent() {
    }
    // 초대 거절

    // 리뷰 작성

    //

}
