package org.example.pdnight.domain.notification.infra;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.notification.application.NotificationConsumeService;
import org.example.pdnight.domain.notification.presentation.dto.event.PostConfirmedEvent;
import org.example.pdnight.global.event.*;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationConsumer {
    private final NotificationConsumeService notificationConsumeService;

    // 모임 성사
    @KafkaListener(topics = "post.participant.confirmed", groupId = "alert-group")
    public void consumePostConfirmedEvent(PostConfirmedEvent event) {
        notificationConsumeService.handlePostConfirmed(event);
    }

    //모임 참여 신청
    @KafkaListener(topics = "post.participant.applied", groupId = "alert-group")
    public void consumeParticipateAppliedEvent(PostParticipateAppliedEvent event) {
        notificationConsumeService.handlePostApplied(event);
    }

    //모임
    @KafkaListener(topics = "followee.post.created", groupId = "alert-group")
    public void consumeFolloweePostCreatedEvent(FolloweePostCreatedEvent event) {
        notificationConsumeService.handleFolloweePostCreated(event);
    }

    // 모임 참여 수락
    @KafkaListener(topics = "post.participant.accepted", groupId = "alert-group")
    public void consumeApplyAcceptedEvent(PostApplyAcceptedEvent event) {
        notificationConsumeService.handlePostApplyAccepted(event);
    }

    // 모임 참여 거절
    @KafkaListener(topics = "post.participant.denied", groupId = "alert-group")
    public void consumeApplyDeniedEvent(PostApplyDeniedEvent event) {
        notificationConsumeService.handlePostApplyDeniedEvent(event);
    }

    // 팔로우한 사람이 게시물 작성
    @KafkaListener(topics = "followee.post.created", groupId = "alert-group")
    public void consumeApplyFolloweePostCreatedEvent(FolloweePostCreatedEvent event) {
        notificationConsumeService.handleFolloweePostCreated(event);
    }

    // 초대 전송
    @KafkaListener(topics = "invite.sent", groupId = "alert-group")
    public void consumeApplyInviteSentEvent(InviteSentEvent event) {
        notificationConsumeService.handleInviteSent(event);
    }

    // 초대 수락


    // 누가 작성 하신거지 이거 알림 관련 아닌 것들은 여기 있을 필요 없습니다.
    // 초대 거절
    // 리뷰 작성
    // 채팅방 생성
    // 회원탈퇴
    // 댓글 작성
    // 대댓글 작성
    // 쿠폰 발행
    // 쿠폰 만료
}
