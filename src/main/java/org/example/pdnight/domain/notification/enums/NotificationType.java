package org.example.pdnight.domain.notification.enums;

public enum NotificationType {

    // ------------알림 용------------//

    /**
     * 모임 참여 신청, 승인, 거절
     */
    PARTICIPANT_APPLIED,
    PARTICIPANT_ACCEPTED,
    PARTICIPANT_REJECTED,

    /**
     * 초대 신청, 승인, 거절
     */
    INVITE_SENT,
    INVITE_ACCEPTED,
    INVITE_REJECTED,

    /**
     * 댓글 작성 시
     */
    POST_COMMENT_CREATED,
    /**
     * 대댓글 작성 시
     */
    COMMENT_REPLY_CREATED,

    /**
     * 모임 확정
     */
    POST_CONFIRMED,

    /**
     * 채팅방 생성
     */
    CHATROOM_CREATED,

    /**
     * 리뷰 받은 사람에게 이벤트 받았다고 이벤트 발행
     */
    REVIEW_SUBMITTED,

    /**
     * 쿠폰 발행
     */
    COUPON_ISSUED,
    /**
     * 쿠폰 만료
     */
    COUPON_EXPIRED,

    /**
     *팔로우 중인 사람 게시물 작성
     */
    FOLLOWEE_POST_CREATED,

    // ----------생성,수정 삭제 용----------//

    /**
     * 리뷰 생성 -> 유저 도메인 쪽에 변경하도록 이벤트 발행
     */
    REVIEW_CREATED,


    /**
     *유저 객체 생성하도록 이벤트 발행
     */
    USER_CREATED,
    USER_DELETED,

    /**
     *모임 삭제
     */
    POST_DELETED,

    /**
     *모임 생성
     */
    POST_CREATED
}