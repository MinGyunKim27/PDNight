package org.example.pdnight.global.common.enums;

public enum KafkaTopic {
    // 모임 관련
    POST_PARTICIPANT_APPLIED("post.participant.applied"),
    POST_PARTICIPANT_ACCEPTED("post.participant.accepted"),
    POST_PARTICIPANT_DENIED("post.participant.denied"),
    POST_PARTICIPANT_CONFIRMED("post.confirmed"),
    POST_CONFIRMED("post.confirmed"),
    POST_DELETED("post.deleted"),

    // 팔로우
    FOLLOWEE_POST_CREATED("followee.post.created"),

    // 초대 관련
    INVITE_SENDED("invite.sended"),
    INVITE_ACCEPTED("invite.accepted"),
    INVITE_DENIED("invite.denied"),

    // 리뷰
    USER_REVIEW_CREATED("user.review.created"),

    // 채팅방
    CHATROOM_CREATED("chatroom.created"),

    // 댓글
    POST_COMMENT_CREATED("post.comment.created"),
    POST_COMMENT_REPLY_CREATED("post.comment.reply.created"),

    // 쿠폰
    COUPON_ISSUED("coupon.issued"),
    COUPON_EXPIRED("coupon.expired"),

    // 인증/회원
    AUTH_SIGNED_UP("auth.signedup"),
    AUTH_DELETED("auth.deleted"),
    USER_DELETED("user.deleted");

    private final String topicName;

    KafkaTopic(String topicName) {
        this.topicName = topicName;
    }

    public String topicName() {
        return topicName;
    }
}
