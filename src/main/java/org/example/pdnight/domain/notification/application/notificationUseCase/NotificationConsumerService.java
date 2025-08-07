package org.example.pdnight.domain.notification.application.notificationUseCase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.pdnight.domain.notification.domain.Notification;
import org.example.pdnight.domain.notification.domain.NotificationCommander;
import org.example.pdnight.domain.notification.enums.NotificationType;
import org.example.pdnight.domain.notification.infra.NotificationSocketSender;
import org.example.pdnight.domain.notification.presentation.dto.NotificationResponse;
import org.example.pdnight.global.common.enums.ErrorCode;
import org.example.pdnight.global.common.exception.BaseException;
import org.example.pdnight.global.event.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationConsumerService {

    private final NotificationCommander notificationCommander;
    private final NotificationSocketSender webSocketSender;

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

    // 사용자 리뷰 작성
    public void handleReviewCreated(ReviewCreatedEvent event) {
        Long receiverId = event.revieweeId();
        Long senderId = event.reviewerId();
        String message ="사용자에게 리뷰가 작성되었습니다.";
        String logMessage = "사용자 리뷰 작성 알림 저장 완료!";
        NotificationType type = NotificationType.REVIEW_CREATED;

        sendMessage(receiverId, senderId, message, logMessage, type);
    }

    // 게시글 채팅방 생성
    public void handleChatroomCreated(ChatroomCreatedEvent event) {
        for(Long userId : event.participantIds()) {
            Long senderId = event.authId();
            String message ="게시글의 채팅방이 생성되었습니다.";
            String logMessage = "게시글 채팅방 생성 알림 저장 완료!";
            NotificationType type = NotificationType.CHATROOM_CREATED;

            sendMessage(userId, senderId, message, logMessage, type);
        }
    }

    // 쿠폰 발행
    public void handleCouponIssued(CouponIssuedEvent event) {
        Long receiverId = event.userId();
        String message ="쿠폰이 발행되었습니다.";
        String logMessage = "쿠폰 발행 알림 저장 완료!";
        NotificationType type = NotificationType.COUPON_ISSUED;

        sendMessage(receiverId, null, message, logMessage, type);
    }

    // 쿠폰 만료 전
    public void handleCouponExpired(CouponExpiredEvent event) {
        List<Long> receiverIds = event.userIds();
        String message ="쿠폰만료까지 1일 남았습니다.";
        String logMessage = "쿠폰 만료 알림 저장 완료!";
        NotificationType type = NotificationType.COUPON_EXPIRED;

        for(Long receiverId : receiverIds) {
            sendMessage(receiverId, null, message, logMessage, type);
        }
    }

    // 댓글 작성
    public void handleCommentCreated(CommentCreatedEvent event) {
        Long receiverId = event.postAuthorId();
        Long senderId = event.commentAuthorId();
        String message = "게시글에 댓글이 작성되었습니다.";
        String logMessage = "댓글 작성 알림 저장 완료!";
        NotificationType type = NotificationType.POST_COMMENT_CREATED;
        sendMessage(receiverId, senderId, message, logMessage, type);
    }

    // 대댓글 작성
    public void handleReplyCreated(CommentReplyCreatedEvent event) {
        Long receiverId = event.commentAuthorId();
        Long senderId = event.replyAuthorId();
        String message = "댓글에 대댓글이 작성되었습니다.";
        String logMessage = "대댓글 작성 알림 저장 완료!";
        NotificationType type = NotificationType.COMMENT_REPLY_CREATED;
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