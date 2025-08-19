package org.example.pdnight.domain.notification.infra;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.example.pdnight.domain.notification.application.notificationUseCase.NotificationConsumerService;
import org.example.pdnight.domain.notification.enums.NotificationType;
import org.example.pdnight.domain.post.domain.post.PostDocument;
import org.example.pdnight.global.event.*;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationConsumer {

    private final NotificationConsumerService notificationConsumerService;
    private final ElasticsearchIndexService elasticsearchIndexService;

    private final List<PostEvent> buffer = new ArrayList<>();
    private final Object lock = new Object();

    private static final int BATCH_SIZE = 500;

    // 팔로우한 사람이 게시물 작성 (작성자 -> 팔로워들)
    @KafkaListener(topics = "followee.post.created", groupId = "alert-social-group", containerFactory = "notificationListenerContainerFactory")
    public void consumeFolloweePostCreatedEvent(FolloweePostCreatedEvent event) {
        notificationConsumerService.many(
                event.followeeIds(),
                event.authorId(),
                "팔로우 중인 유저가 게시물을 생성했습니다!",
                "팔로우 게시물 생성 알림 저장 완료!",
                NotificationType.FOLLOWEE_POST_CREATED
        );
    }

    // 초대 전송 (작성자 -> 대상 유저)
    @KafkaListener(topics = "invite.sent", groupId = "alert-invite-group", containerFactory = "notificationListenerContainerFactory")
    public void consumeApplyInviteSentEvent(InviteSentEvent event) {
        notificationConsumerService.one(
                event.userId(),
                event.authorId(),
                "모임 초대 전송되었습니다",
                "모임 초대 전송 알림 저장 완료!",
                NotificationType.INVITE_SENT
        );
    }

    // 초대 수락 (수락자 -> 작성자)
    @KafkaListener(topics = "invite.accepted", groupId = "alert-invite-group", containerFactory = "notificationListenerContainerFactory")
    public void consumeApplyInviteAcceptedEvent(InviteAcceptedEvent event) {
        notificationConsumerService.one(
                event.authorId(),
                event.userId(),
                "모임 초대 수락되었습니다",
                "모임 초대 수락 알림 저장 완료!",
                NotificationType.INVITE_ACCEPTED
        );
    }

    // 초대 거절 (거절자 -> 작성자)
    @KafkaListener(topics = "invite.denied", groupId = "alert-invite-group", containerFactory = "notificationListenerContainerFactory")
    public void consumeApplyInviteDeniedEvent(InviteDeniedEvent event) {
        notificationConsumerService.one(
                event.authorId(),
                event.userId(),
                "모임 초대 거절되었습니다",
                "모임 초대 거절 알림 저장 완료!",
                NotificationType.INVITE_REJECTED
        );
    }

    // 리뷰 작성 (리뷰어 -> 피평가자)
    @KafkaListener(topics = "user.review.created", groupId = "alert-review-group", containerFactory = "notificationListenerContainerFactory")
    public void consumeReviewCreatedEvent(ReviewCreatedEvent event) {
        notificationConsumerService.one(
                event.revieweeId(),
                event.reviewerId(),
                "사용자에게 리뷰가 작성되었습니다.",
                "사용자 리뷰 작성 알림 저장 완료!",
                NotificationType.REVIEW_CREATED
        );
    }

    // 게시글 채팅방 생성 (시스템/작성자 -> 참여자들)
    @KafkaListener(topics = "chatroom.created", groupId = "chat-create-group", containerFactory = "notificationListenerContainerFactory")
    public void consumeChatroomCreatedEvent(ChatroomCreatedEvent event) {
        notificationConsumerService.many(
                event.participantIds(),
                event.authId(),
                "게시글의 채팅방이 생성되었습니다.",
                "게시글 채팅방 생성 알림 저장 완료!",
                NotificationType.CHATROOM_CREATED
        );
    }

    // 댓글 작성 (댓글 작성자 -> 게시글 작성자)
    @KafkaListener(topics = "post.comment.created", groupId = "alert-comment-group", containerFactory = "notificationListenerContainerFactory")
    public void consumeCommentCreatedEvent(CommentCreatedEvent event) {
        notificationConsumerService.one(
                event.postAuthorId(),
                event.commentAuthorId(),
                "게시글에 댓글이 작성되었습니다.",
                "댓글 작성 알림 저장 완료!",
                NotificationType.POST_COMMENT_CREATED
        );
    }

    // 대댓글 작성 (대댓글 작성자 -> 원댓글 작성자)
    // ⚠️ 주의: 토픽이 실제로 별도라면 "post.reply.created" 등 실제 토픽명으로 바꿔주세요.
    @KafkaListener(topics = "post.reply.created", groupId = "alert-comment-group", containerFactory = "notificationListenerContainerFactory")

    public void consumeReplyCreatedEvent(CommentReplyCreatedEvent event) {
        notificationConsumerService.one(
                event.commentAuthorId(),
                event.replyAuthorId(),
                "댓글에 대댓글이 작성되었습니다.",
                "대댓글 작성 알림 저장 완료!",
                NotificationType.COMMENT_REPLY_CREATED
        );
    }

    // 쿠폰 발행 (시스템 -> 유저)
    @KafkaListener(topics = "coupon.issued", groupId = "alert-coupon-group", containerFactory = "notificationListenerContainerFactory")
    public void consumeApplyCouponIssuedEvent(CouponIssuedEvent event) {
        notificationConsumerService.one(
                event.userId(),
                null,
                "쿠폰이 발행되었습니다.",
                "쿠폰 발행 알림 저장 완료!",
                NotificationType.COUPON_ISSUED
        );
    }

    // 쿠폰 만료 (시스템 -> 유저들)
    @KafkaListener(topics = "coupon.expired", groupId = "alert-coupon-group", containerFactory = "notificationListenerContainerFactory")
    public void consumeApplyCouponExpiredEvent(CouponExpiredEvent event) {
        List<Long> userIds = event.userIds();
        notificationConsumerService.many(
                userIds,
                null,
                "쿠폰만료까지 1일 남았습니다.",
                "쿠폰 만료 알림 저장 완료!",
                NotificationType.COUPON_EXPIRED
        );
    }

    // 모임 성사 - 참가자들에게 다건, 작성자에게 단건
    @KafkaListener(topics = {"post.confirmed"}, groupId = "alert-post-group", containerFactory = "notificationListenerContainerFactory")
    public void consumePostConfirmedEvent(PostConfirmedEvent event) {
        // 참가자 알림
        notificationConsumerService.many(
                event.confirmedUserIds(),
                event.authorId(),
                "모임이 확정되었습니다!",
                "모임 확정 알림 저장 완료!",
                NotificationType.POST_CONFIRMED
        );
        // 작성자 알림
        notificationConsumerService.one(
                event.authorId(),
                event.authorId(),
                "생성하신 모임이 확정되었습니다!",
                "모임 확정 알림 저장 완료!",
                NotificationType.POST_CONFIRMED
        );
    }

    // 모임 참여 신청 (신청자 -> 작성자)

    @KafkaListener(topics = "post.participant.applied", groupId = "alert-post-group", containerFactory = "notificationListenerContainerFactory", concurrency = "24")

    public void consumeParticipateAppliedEvent(PostParticipateAppliedEvent event) {
        notificationConsumerService.one(
                event.authorId(),
                event.applierId(),
                "모임 참가 신청되었습니다",
                "모임 참가 신청 알림 저장 완료!",
                NotificationType.PARTICIPANT_APPLIED
        );
    }

    // 모임 참여 수락 (작성자 -> 신청자)
    @KafkaListener(topics = "post.participant.accepted", groupId = "alert-post-group", containerFactory = "notificationListenerContainerFactory")
    public void consumeApplyAcceptedEvent(PostApplyAcceptedEvent event) {
        notificationConsumerService.one(
                event.applierId(),
                event.authorId(),
                "모임 참가 수락되었습니다",
                "모임 참가 수락 알림 저장 완료!",
                NotificationType.PARTICIPANT_ACCEPTED
        );
    }

    // 모임 참여 거절 (작성자 -> 신청자)
    @KafkaListener(topics = "post.participant.denied", groupId = "alert-post-group", containerFactory = "notificationListenerContainerFactory")
    public void consumeApplyDeniedEvent(PostApplyDeniedEvent event) {
        notificationConsumerService.one(
                event.applierId(),
                event.authorId(),
                "모임 참여 거절되었습니다",
                "모임 참여 거절 알림 저장 완료!",
                NotificationType.PARTICIPANT_REJECTED
        );
    }

    // Kafka로부터 PostEvent 메시지를 받아서 일정량 모아두었다가 Elasticsearch에 bulk insert
    @KafkaListener(
            topics = "post",
            groupId = "search-indexer-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumePostEvent(PostEvent event) {
        log.info("Consuming PostEvent: {}", event);

        try {
            PostDocument document = event.document();   // 이벤트로부터 문서 추출
            log.info("Valid PostDocument extracted, id={}", document.getId());

            // 동기화 블록
            synchronized (lock) {
                buffer.add(event);  // 이벤트를 버퍼에 추가
                log.info("Added PostEvent to buffer. Current buffer size: {}", buffer.size());

                // 버퍼가 BATCH_SIZE 이상이면 즉시 flush 실행
                if (buffer.size() >= BATCH_SIZE) {
                    flush();
                }
            }
        } catch (Exception e) {
            log.error("Failed to process PostEvent: {}", event, e);
            throw e;
        }
    }

    @Scheduled(fixedDelay = 10000) // 10초마다 강제 flush
    public void scheduledFlush() {
        synchronized (lock) {
            if (!buffer.isEmpty()) {
                flush();
            }
        }
    }

    // 버퍼의 이벤트들을 Elasticsearch에 bulk insert하는 메서드
    private void flush() {
        if (buffer.isEmpty()) {
            return;
        }

        // 현재 버퍼 내용을 복사해서 작업용 리스트로 만들고, 버퍼는 비움
        List<PostEvent> currentBatch = new ArrayList<>(buffer);
        buffer.clear();

        int maxRetries = 3;
        int attempt = 0;
        boolean success = false;

        // 실패 시 최대 3번까지 재시도
        while (attempt < maxRetries && !success) {
            try {
                attempt++;
                log.info("Flushing attempt {} for {} PostEvents", attempt, currentBatch.size());
                List<PostDocument> documents = currentBatch.stream()
                        .map(PostEvent::document)
                        .toList();

                elasticsearchIndexService.bulkIndexPosts(documents);
                log.info("Successfully bulk indexed {} posts", documents.size());
                success = true;

            } catch (Exception e) {
                // 실패 시 로그 기록 후 2초 대기
                log.error("Failed to bulk index batch (attempt {}/{}). Retrying...", attempt, maxRetries, e);

                try {
                    Thread.sleep(2000L); // 2초 대기 후 재시도
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        if (!success) {
            // 실패하면 다시 버퍼에 추가 (맨 앞에 넣어서 우선 처리)
            buffer.addAll(0, currentBatch);
            log.error("Giving up after {} attempts. Returning batch to buffer.", maxRetries);
        }
    }


    // 알림 관련 DLT
    @KafkaListener(
            topicPattern = ".*\\.notification\\.DLT$",
            groupId = "DLT-handler",
            containerFactory = "notificationDltListenerContainerFactory"
    )
    public void dltConsumer(ConsumerRecord<String, Object> record) {

        switch (record.topic()) {
            case "post.confirmed.notification.DLT": {
                PostConfirmedEvent event = (PostConfirmedEvent) record.value();
                notificationConsumerService.many(
                        event.confirmedUserIds(),
                        event.authorId(),
                        "모임이 확정되었습니다!",
                        "모임 확정 알림 저장 완료!",
                        NotificationType.POST_CONFIRMED
                );
                notificationConsumerService.one(
                        event.authorId(),
                        event.authorId(),
                        "생성하신 모임이 확정되었습니다!",
                        "모임 확정 알림 저장 완료!",
                        NotificationType.POST_CONFIRMED
                );
                break;
            }
            case "post.participant.applied.notification.DLT": {
                PostParticipateAppliedEvent event = (PostParticipateAppliedEvent) record.value();
                notificationConsumerService.one(
                        event.applierId(),
                        event.authorId(),
                        "모임 참가 신청되었습니다",
                        "모임 참가 신청 알림 저장 완료!",
                        NotificationType.PARTICIPANT_APPLIED
                );
                break;
            }
            case "post.participant.accepted.notification.DLT": {
                PostApplyAcceptedEvent event = (PostApplyAcceptedEvent) record.value();
                notificationConsumerService.one(
                        event.applierId(),
                        event.authorId(),
                        "모임 참가 수락되었습니다",
                        "모임 참가 수락 알림 저장 완료!",
                        NotificationType.PARTICIPANT_ACCEPTED
                );
                break;
            }
            case "post.participant.denied.notification.DLT": {
                PostApplyDeniedEvent event = (PostApplyDeniedEvent) record.value();
                notificationConsumerService.one(
                        event.applierId(),
                        event.authorId(),
                        "모임 참여 거절되었습니다",
                        "모임 참여 거절 알림 저장 완료!",
                        NotificationType.PARTICIPANT_REJECTED
                );
                break;
            }
            case "followee.post.created.notification.DLT": {
                FolloweePostCreatedEvent event = (FolloweePostCreatedEvent) record.value();
                notificationConsumerService.many(
                        event.followeeIds(),
                        event.authorId(),
                        "팔로우 중인 유저가 게시물을 생성했습니다!",
                        "팔로우 게시물 생성 알림 저장 완료!",
                        NotificationType.FOLLOWEE_POST_CREATED
                );
                break;
            }
            case "invite.accepted.notification.DLT": {
                InviteAcceptedEvent event = (InviteAcceptedEvent) record.value();

                notificationConsumerService.one(
                        event.authorId(),
                        event.userId(),
                        "모임 초대 수락되었습니다",
                        "모임 초대 수락 알림 저장 완료!",
                        NotificationType.INVITE_ACCEPTED
                );

                break;
            }
            case "invite.sent.notification.DLT": {
                InviteSentEvent event = (InviteSentEvent) record.value();

                notificationConsumerService.one(
                        event.userId(),
                        event.authorId(),
                        "모임 초대 전송되었습니다",
                        "모임 초대 전송 알림 저장 완료!",
                        NotificationType.INVITE_SENT
                );

                break;
            }
            case "invite.denied.notification.DLT": {
                InviteDeniedEvent event = (InviteDeniedEvent) record.value();

                notificationConsumerService.one(
                        event.authorId(),
                        event.userId(),
                        "모임 초대 거절되었습니다",
                        "모임 초대 거절 알림 저장 완료!",
                        NotificationType.INVITE_REJECTED
                );

                break;
            }
            case "user.review.created.notification.DLT": {
                ReviewCreatedEvent event = (ReviewCreatedEvent) record.value();
                notificationConsumerService.one(
                        event.revieweeId(),
                        event.reviewerId(),
                        "사용자에게 리뷰가 작성되었습니다.",
                        "사용자 리뷰 작성 알림 저장 완료!",
                        NotificationType.REVIEW_CREATED
                );
                break;
            }
            case "chatroom.created.notification.DLT": {
                ChatroomCreatedEvent event = (ChatroomCreatedEvent) record.value();

                notificationConsumerService.many(
                        event.participantIds(),
                        event.authId(),
                        "게시글의 채팅방이 생성되었습니다.",
                        "게시글 채팅방 생성 알림 저장 완료!",
                        NotificationType.CHATROOM_CREATED
                );

                break;
            }
            case "coupon.issued.notification.DLT": {
                CouponIssuedEvent event = (CouponIssuedEvent) record.value();
                notificationConsumerService.one(
                        event.userId(),
                        null,
                        "쿠폰이 발행되었습니다.",
                        "쿠폰 발행 알림 저장 완료!",
                        NotificationType.COUPON_ISSUED
                );

                break;
            }
            case "coupon.expired.notification.DLT": {
                CouponExpiredEvent event = (CouponExpiredEvent) record.value();
                List<Long> userIds = event.userIds();
                notificationConsumerService.many(
                        userIds,
                        null,
                        "쿠폰 만료까지 1일 남았습니다.",
                        "쿠폰 만료 알림 저장 완료!",
                        NotificationType.COUPON_EXPIRED
                );

                break;
            }
            case "post.comment.created.notification.DLT": {
                CommentCreatedEvent event = (CommentCreatedEvent) record.value();

                notificationConsumerService.one(
                        event.postAuthorId(),
                        event.commentAuthorId(),
                        "게시글에 댓글이 작성되었습니다.",
                        "댓글 작성 알림 저장 완료!",
                        NotificationType.POST_COMMENT_CREATED
                );

                break;
            }
            case "post.reply.created.notification.DLT": {
                CommentReplyCreatedEvent event = (CommentReplyCreatedEvent) record.value();

                notificationConsumerService.one(
                        event.commentAuthorId(),
                        event.replyAuthorId(),
                        "댓글에 대댓글이 작성되었습니다.",
                        "대댓글 작성 알림 저장 완료!",
                        NotificationType.COMMENT_REPLY_CREATED
                );
                break;
            }

            default:
                log.error("지정 되지 않은 토픽이 들어옴, topic.name = {}", record.topic());
                break;
        }
    }
}

