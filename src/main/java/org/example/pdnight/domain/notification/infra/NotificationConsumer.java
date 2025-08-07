package org.example.pdnight.domain.notification.infra;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.notification.application.notificationUseCase.NotificationConsumerService;
import org.example.pdnight.global.event.*;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationConsumer {
    private final NotificationConsumerService notificationConsumerService;

    // 모임 성사
    @KafkaListener(topics = "post.participant.confirmed", groupId = "alert-group")
    public void consumePostConfirmedEvent(PostConfirmedEvent event) {
        notificationConsumerService.handlePostConfirmed(event);
    }

    //모임 참여 신청
    @KafkaListener(topics = "post.participant.applied", groupId = "alert-group")
    public void consumeParticipateAppliedEvent(PostParticipateAppliedEvent event) {
        notificationConsumerService.handlePostApplied(event);
    }

    // 팔로우한 사람이 게시물 작성
    @KafkaListener(topics = "followee.post.created", groupId = "alert-group")
    public void consumeFolloweePostCreatedEvent(FolloweePostCreatedEvent event) {
        notificationConsumerService.handleFolloweePostCreated(event);
    }

    // 모임 참여 수락
    @KafkaListener(topics = "post.participant.accepted", groupId = "alert-group")
    public void consumeApplyAcceptedEvent(PostApplyAcceptedEvent event) {
        notificationConsumerService.handlePostApplyAccepted(event);
    }

    // 모임 참여 거절
    @KafkaListener(topics = "post.participant.denied", groupId = "alert-group")
    public void consumeApplyDeniedEvent(PostApplyDeniedEvent event) {
        notificationConsumerService.handlePostApplyDeniedEvent(event);
    }

    // 초대 전송
    @KafkaListener(topics = "invite.sent", groupId = "alert-group")
    public void consumeApplyInviteSentEvent(InviteSentEvent event) {
        notificationConsumerService.handleInviteSent(event);
    }

    // 초대 수락
    @KafkaListener(topics = "invite.accepted", groupId = "alert-group")
    public void consumeApplyInviteAcceptedEvent(InviteAcceptedEvent event) {
        notificationConsumerService.handleInviteAcceptedEvent(event);
    }

    // 초대 거절
    @KafkaListener(topics = "invite.denied", groupId = "alert-group")
    public void consumeApplyInviteDeniedEvent(InviteDeniedEvent event) {
        notificationConsumerService.handleInviteDeniedEvent(event);
    }

    // 리뷰 작성
    // 채팅방 생성
    // 댓글 작성
    // 대댓글 작성
    // 쿠폰 발행

    // 리뷰 작성 -
    @KafkaListener(topics = "user.review.created", groupId = "alert-group")
    public void consumeReviewCreatedEvent(ReviewCreatedEvent event) {
        notificationConsumeService.handleReviewCreated(event);
    }

    // 게시글 채팅방 생성 -
    @KafkaListener(topics = "chatroom.created", groupId = "alert-group")
    public void consumeChatroomCreatedEvent(ChatroomCreatedEvent event) {
        notificationConsumeService.handleChatroomCreated(event);
    }

    // 댓글 작성  -
    @KafkaListener(topics = "post.comment.created", groupId = "alert-group")
    public void consumeCommentCreatedEvent(CommentCreatedEvent event) {
        notificationConsumeService.handleCommentCreated(event);
    }

    // 대댓글 작성 -
    @KafkaListener(topics = "post.comment.created", groupId = "alert-group")
    public void consumeReplyCreatedEvent(CommentReplyCreatedEvent event) {
        notificationConsumeService.handleReplyCreated(event);
    }

    // 쿠폰 발행 -
    @KafkaListener(topics = "coupon.issued", groupId = "alert-group")
    public void consumeApplyCouponIssuedEvent(CouponIssuedEvent event) {
        notificationConsumeService.handleCouponIssued(event);
    }

    // 쿠폰 만료
    @KafkaListener(topics = "coupon.expired", groupId = "alert-group")
    public void consumeApplyCouponExpiredEvent(CouponExpiredEvent event) {
        notificationConsumeService.handleCouponExpired(event);
    }
}
