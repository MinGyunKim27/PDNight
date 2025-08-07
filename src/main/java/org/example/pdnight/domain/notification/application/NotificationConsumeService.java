package org.example.pdnight.domain.notification.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.pdnight.domain.notification.domain.Notification;
import org.example.pdnight.domain.notification.domain.NotificationCommander;
import org.example.pdnight.domain.notification.domain.NotificationReader;
import org.example.pdnight.domain.notification.enums.NotificationType;
import org.example.pdnight.domain.notification.infra.NotificationSocketSender;
import org.example.pdnight.domain.notification.presentation.dto.NotificationResponse;
import org.example.pdnight.global.event.PostConfirmedEvent;
import org.example.pdnight.global.common.exception.BaseException;
import org.example.pdnight.global.event.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

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
        String message;
        String logMessage = "모임 확정 알림 저장 완료!";
        Long receiverId;
        Long senderId = event.authorId();
        NotificationType type = NotificationType.POST_CONFIRMED;

        // 참가자들에게 전송
        for (Long userId : event.confirmedUserIds()) {
            receiverId = userId;
            message = "신청하신 모임이 확정되었습니다!";

            sendMessage(receiverId, senderId, message, logMessage, type);
        }

        // 작성자에게도 전송
        message = "생성하신 모임이 확정되었습니다!";
        receiverId = event.authorId();

        sendMessage(receiverId, senderId, message, logMessage, type);
    }

    // 팔로잉 중인 사람이 게시글 생성
    public void handleFolloweePostCreated(FolloweePostCreatedEvent event) {
        // 나를 팔로우 중인 사람들에게 전송
        String message = "팔로우 중인 유저가 게시물을 생성했습니다!";
        String logMessage = "팔로우 게시물 생성 알림 저장 완료!";
        Long senderId = event.authorId();
        NotificationType type = NotificationType.FOLLOWEE_POST_CREATED;

        List<Long> followeeIds = event.followeeIds();

        for (Long followeeId : followeeIds) {
            sendMessage(followeeId, senderId, message, logMessage, type);
        }
    }


    // 모임 참가 신청 알림
    public void handlePostApplied(PostParticipateAppliedEvent event) {
        // 신청자 -> 모임 작성자에게 전송
        String message = "모임 참가 신청되었습니다";
        String logMessage = "모임 참가 신청 알림 저장 완료!";
        Long receiverId = event.authorId();
        Long senderId = event.applierId();
        NotificationType type = NotificationType.PARTICIPANT_APPLIED;

        sendMessage(receiverId, senderId, message, logMessage, type);
    }

    // 모임 참여 수락
    public void handlePostApplyAccepted(PostApplyAcceptedEvent event) {
        // 모임 작성자 -> 신청자에게 전송
        String message = "모임 참가 수락되었습니다";
        String logMessage = "모임 참가 수락 알림 저장 완료!";
        Long receiverId = event.applierId();
        Long senderId = event.authorId();
        NotificationType type = NotificationType.PARTICIPANT_ACCEPTED;

        sendMessage(receiverId, senderId, message, logMessage, type);
    }

    // 모임 참여 거절
    public void handlePostApplyDeniedEvent(PostApplyDeniedEvent event) {
        // 모임 작성자 -> 신청자에게 전송
        String message = "모임 참여 거절되었습니다";
        String logMessage = "모임 참여 거절 알림 저장 완료!";
        Long receiverId = event.applierId();
        Long senderId = event.authorId();
        NotificationType type = NotificationType.PARTICIPANT_REJECTED;

        sendMessage(receiverId, senderId, message, logMessage, type);
    }

    // 초대 전송
    public void handleInviteSent(InviteSentEvent event) {
        // 모임 작성자 -> 초대할 사람에게 전송
        String message = "모임 초대 전송되었습니다";
        String logMessage = "모임 초대 전송 알림 저장 완료!";
        Long receiverId = event.userId();
        Long senderId = event.authorId();
        NotificationType type = NotificationType.INVITE_SENT;

        sendMessage(receiverId, senderId, message, logMessage, type);
    }

    // 초대 수락
    public void handleInviteAcceptedEvent(InviteAcceptedEvent event) {
        // 초대 받은 사람 -> 모임 작성자 에게 전송
        String message = "모임 초대 수락되었습니다";
        String logMessage = "모임 초대 수락 알림 저장 완료!";
        Long receiverId = event.authorId();
        Long senderId = event.userId();
        NotificationType type = NotificationType.INVITE_ACCEPTED;

        sendMessage(receiverId, senderId, message, logMessage, type);
    }

    // 초대 거절
    public void handleInviteDeniedEvent(InviteDeniedEvent event) {
        // 초대 받은 사람 -> 모임 작성자 에게 전송
        String message = "모임 초대 거절되었습니다";
        String logMessage = "모임 초대 거절 알림 저장 완료!";
        Long receiverId = event.authorId();
        Long senderId = event.userId();
        NotificationType type = NotificationType.INVITE_REJECTED;

        sendMessage(receiverId, senderId, message, logMessage, type);
    }


    private void sendMessage(Long receiverId, Long senderId, String message, String logMessage, NotificationType type) {
        Notification notification = Notification.from(receiverId, message, senderId, type);
        NotificationResponse dto = NotificationResponse.from(notification);
        notificationCommander.save(notification);
        log.info(logMessage + "userId : {}", receiverId);
        webSocketSender.sendToUser(receiverId, dto);
    }
}
