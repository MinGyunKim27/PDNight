package org.example.pdnight.domain.notification.infra;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.pdnight.domain.notification.application.notificationUseCase.NotificationConsumerService;
import org.example.pdnight.global.event.*;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationConsumer {
    private final NotificationConsumerService notificationConsumerService;

    // 모임 성사
    @KafkaListener(topics = "post.confirmed", groupId = "alert-group")
    public void consumePostConfirmedEvent(
            PostConfirmedEvent event,
            @Header(value = "traceId", required = false) String traceId,
            @Header(value = "userId", required = false) String userId
    ) {
        log.info("[traceId={} userId={}] post.confirmed event received", traceId, userId);
        notificationConsumerService.handlePostConfirmed(event);
    }

    //모임 참여 신청
    @KafkaListener(topics = "post.participant.applied", groupId = "alert-group")
    public void consumeParticipateAppliedEvent(
            PostParticipateAppliedEvent event,
            @Header(value = "traceId", required = false) String traceId,
            @Header(value = "userId", required = false) String userId
    ) {
        log.info("[traceId={} userId={}] post.participant.applied event received", traceId, userId);
        notificationConsumerService.handlePostApplied(event);
    }

    // 팔로우한 사람이 게시물 작성
    @KafkaListener(topics = "followee.post.created", groupId = "alert-group")
    public void consumeFolloweePostCreatedEvent(
            FolloweePostCreatedEvent event,
            @Header(value = "traceId", required = false) String traceId,
            @Header(value = "userId", required = false) String userId
    ) {
        log.info("[traceId={} userId={}] followee.post.created event received", traceId, userId);
        notificationConsumerService.handleFolloweePostCreated(event);
    }

    // 모임 참여 수락
    @KafkaListener(topics = "post.participant.accepted", groupId = "alert-group")
    public void consumeApplyAcceptedEvent(
            PostApplyAcceptedEvent event,
            @Header(value = "traceId", required = false) String traceId,
            @Header(value = "userId", required = false) String userId
    ) {
        log.info("[traceId={} userId={}] post.participant.accepted event received", traceId, userId);
        notificationConsumerService.handlePostApplyAccepted(event);
    }

    // 모임 참여 거절
    @KafkaListener(topics = "post.participant.denied", groupId = "alert-group")
    public void consumeApplyDeniedEvent(
            PostApplyDeniedEvent event,
            @Header(value = "traceId", required = false) String traceId,
            @Header(value = "userId", required = false) String userId
    ) {
        log.info("[traceId={} userId={}] post.participant.denied event received", traceId, userId);
        notificationConsumerService.handlePostApplyDeniedEvent(event);
    }

    // 초대 전송
    @KafkaListener(topics = "invite.sent", groupId = "alert-group")
    public void consumeApplyInviteSentEvent(
            InviteSentEvent event,
            @Header(value = "traceId", required = false) String traceId,
            @Header(value = "userId", required = false) String userId
    ) {
        log.info("[traceId={} userId={}] invite.sent event received", traceId, userId);
        notificationConsumerService.handleInviteSent(event);
    }

    // 초대 수락
    @KafkaListener(topics = "invite.accepted", groupId = "alert-group")
    public void consumeApplyInviteAcceptedEvent(
            InviteAcceptedEvent event,
            @Header(value = "traceId", required = false) String traceId,
            @Header(value = "userId", required = false) String userId
    ) {
        log.info("[traceId={} userId={}] invite.accepted event received", traceId, userId);
        notificationConsumerService.handleInviteAcceptedEvent(event);
    }

    // 초대 거절
    @KafkaListener(topics = "invite.denied", groupId = "alert-group")
    public void consumeApplyInviteDeniedEvent(
            InviteDeniedEvent event,
            @Header(value = "traceId", required = false) String traceId,
            @Header(value = "userId", required = false) String userId
    ) {
        log.info("[traceId={} userId={}] invite.denied event received", traceId, userId);
        notificationConsumerService.handleInviteDeniedEvent(event);
    }

    // 리뷰 작성 -
    @KafkaListener(topics = "user.review.created", groupId = "alert-group")
    public void consumeReviewCreatedEvent(
            ReviewCreatedEvent event,
            @Header(value = "traceId", required = false) String traceId,
            @Header(value = "userId", required = false) String userId
    ) {
        log.info("[traceId={} userId={}] user.review.created event received", traceId, userId);
        notificationConsumerService.handleReviewCreated(event);
    }

    // 게시글 채팅방 생성 -
    @KafkaListener(topics = "chatroom.created", groupId = "alert-group")
    public void consumeChatroomCreatedEvent(
            ChatroomCreatedEvent event,
            @Header(value = "traceId", required = false) String traceId,
            @Header(value = "userId", required = false) String userId
    ) {
        log.info("[traceId={} userId={}] chatroom.created event received", traceId, userId);
        notificationConsumerService.handleChatroomCreated(event);
    }

    // 댓글 작성  -
    @KafkaListener(topics = "post.comment.created", groupId = "alert-group")
    public void consumeCommentCreatedEvent(
            CommentCreatedEvent event,
            @Header(value = "traceId", required = false) String traceId,
            @Header(value = "userId", required = false) String userId
    ) {
        log.info("[traceId={} userId={}] post.comment.created event received", traceId, userId);
        notificationConsumerService.handleCommentCreated(event);
    }

    // 대댓글 작성 -
    @KafkaListener(topics = "post.comment.created", groupId = "alert-group")
    public void consumeReplyCreatedEvent(
            CommentReplyCreatedEvent event,
            @Header(value = "traceId", required = false) String traceId,
            @Header(value = "userId", required = false) String userId
    ) {
        log.info("[traceId={} userId={}] post.comment.reply.created event received", traceId, userId);
        notificationConsumerService.handleReplyCreated(event);
    }

    // 쿠폰 발행 -
    @KafkaListener(topics = "coupon.issued", groupId = "alert-group")
    public void consumeApplyCouponIssuedEvent(
            CouponIssuedEvent event,
            @Header(value = "traceId", required = false) String traceId,
            @Header(value = "userId", required = false) String userId
    ) {
        log.info("[traceId={} userId={}] coupon.issued event received", traceId, userId);
        notificationConsumerService.handleCouponIssued(event);
    }

    // 쿠폰 만료
    @KafkaListener(topics = "coupon.expired", groupId = "alert-group")
    public void consumeApplyCouponExpiredEvent(
            CouponExpiredEvent event,
            @Header(value = "traceId", required = false) String traceId,
            @Header(value = "userId", required = false) String userId
    ) {
        log.info("[traceId={} userId={}] coupon.expired event received", traceId, userId);
        notificationConsumerService.handleCouponExpired(event);
    }
}
