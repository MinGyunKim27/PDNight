package org.example.pdnight.domain.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // 인증 관련 에러 (401 Unauthorized)
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다"),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "만료된 토큰입니다"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증이 필요합니다"),
    INSUFFICIENT_PERMISSION(HttpStatus.FORBIDDEN, "권한이 부족합니다"),


    // 사용자 관련 에러 (400 Bad Request, 401 UNAUTHORIZED, 404 NOT_FOUND, 409 Conflict)
    USERNAME_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 존재하는 사용자명입니다"),
    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 존재하는 이메일입니다"),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다"),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다"),
    USER_DEACTIVATED(HttpStatus.UNAUTHORIZED, "탈퇴 된 회원입니다."),

    // 게시물 관련 에러 (400 Bad Request,  404 NOT_FOUND)
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "게시물을 찾을 수 없습니다"),
    TASK_DEACTIVATED(HttpStatus.UNAUTHORIZED, "삭제 된 Task 입니다."),
    TASK_STATE_NOT_REVERSIBLE(HttpStatus.BAD_REQUEST, "Task 상태는 이전 단계로 되돌릴 수 없습니다"),
    TASK_STATE_FLOW_ERROR(HttpStatus.BAD_REQUEST, "Task 상태 변경 흐름이 올바르지 않습니다."),
    POST_FORBIDDEN(HttpStatus.FORBIDDEN, "작성자만 접근할 수 있습니다."),

    // 게시물 좋아요 관련 에러
    POSTLIKE_NOT_FOUND(HttpStatus.NOT_FOUND, "게시물 좋아요를 찾을 수 없습니다"),
    ALREADY_LIKED(HttpStatus.CONFLICT, "이미 좋아요를 누른 게시글입니다."),

    // 게시물 신청 관련 에러
    CANNOT_PARTICIPATE_SELF(HttpStatus.BAD_REQUEST, "본인 게시글엔 신청할 수 없습니다."),
    POST_ALREADY_PENDING(HttpStatus.CONFLICT, "이미 신청했습니다."),
    POST_ALREADY_ACCEPTED(HttpStatus.CONFLICT, "이미 가입되어있습니다."),
    CANNOT_CANCEL(HttpStatus.BAD_REQUEST, "취소할 수 없습니다."),
    NOT_PARTICIPATED(HttpStatus.BAD_REQUEST, "신청되어있지 않습니다."),
    NOT_CHANGE_PENDING(HttpStatus.BAD_REQUEST, "대기 상태로 만들 수 없습니다."),
    NO_VIEWING_PERMISSION(HttpStatus.FORBIDDEN, "조회 권한이 없습니다."),
    NO_UPDATE_PERMISSION(HttpStatus.FORBIDDEN, "수정 권한이 없습니다."),
    CANNOT_PARTICIPATE_POST(HttpStatus.FORBIDDEN,"참가 인원이 모두 찼습니다."),

    // 게시물 신청 조건 불일치
    AGE_LIMIT_NOT_SATISFIED(HttpStatus.FORBIDDEN, "요구하는 연령대 조건에 부합하지 않습니다."),
    GENDER_LIMIT_NOT_SATISFIED(HttpStatus.FORBIDDEN, "요구하는 성별 조건에 부합하지 않습니다."),
    JOB_CATEGORY_LIMIT_NOT_SATISFIED(HttpStatus.FORBIDDEN, "요구하는 직군 조건에 부합하지 않습니다."),

    //초대 관련 에러
    INVITE_ALREADY_EXISTS(HttpStatus.CONFLICT,"이미 초대 한 사람입니다."),
    INVITE_NOT_FOUND(HttpStatus.NOT_FOUND,"초대가 존재하지 않습니다."),
    INVITE_UNAUTHORIZED(HttpStatus.FORBIDDEN,"본인이 한 초대가 아닙니다."),

    // 사용자 리뷰 관련 에러
    CANNOT_REVIEW_SELF(HttpStatus.BAD_REQUEST, "리뷰 작성자는 본인을 평가할 수 없습니다."),
    ALREADY_REVIEWED(HttpStatus.BAD_REQUEST, "이미 해당 게시글에 리뷰를 작성했습니다."),

    // 댓글 관련 에러 (400 Bad Request,  404 NOT_FOUND)
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "댓글을 찾을 수 없습니다"),
    COMMENT_FORBIDDEN(HttpStatus.FORBIDDEN, "작성자만 접근할 수 있습니다."),
    POST_NOT_MATCHED(HttpStatus.CONFLICT, "댓글이 해당 게시글에 속하지 않습니다."),
    INVALID_COMMENT_DEPTH(HttpStatus.BAD_REQUEST, "대댓글에는 대댓글을 달 수 없습니다."),

    // 입력값 검증 에러 (400 Bad Request)
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "입력값이 올바르지 않습니다"),
    MISSING_REQUIRED_FIELD(HttpStatus.BAD_REQUEST, "필수 입력값이 누락되었습니다"),

    // 서버 오류 (500 Internal Server Error)
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "처리 중 오류가 발생했습니다"),

    //취미 관련 오류
    HOBBY_ALREADY_EXISTS(HttpStatus.CONFLICT,"이미 존재하는 취미 입니다"),
    HOBBY_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 취미입니다."),

    //기술 스택 관련 오류
    TECH_STACK_ALREADY_EXISTS(HttpStatus.CONFLICT,"이미 존재하는 기술 스택입니다"),
    TECH_STACK_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 기술 스택입니다."),

    // 이벤트 관련 오류
    EVENT_NOT_FOUNT(HttpStatus.NOT_FOUND, "존재하지 않는 이벤트입니다."),
    EVENT_ALREADY_PENDING(HttpStatus.CONFLICT, "이미 신청한 이벤트입니다."),
    EVENT_PARTICIPANT_FULL(HttpStatus.CONFLICT, "신청 마감되었습니다."),

    // 쿠폰 관련 에러
    COUPON_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 쿠폰입니다."),
    COUPON_ALREADY_USED(HttpStatus.BAD_REQUEST, "이미 사용된 쿠폰입니다."),
    COUPON_EXPIRED(HttpStatus.BAD_REQUEST, "만료된 쿠폰입니다."),
    COUPON_FORBIDDEN(HttpStatus.FORBIDDEN, "본인의 쿠폰만 사용할 수 있습니다."),


    //팔로우 관련 오류
    INVALID_FOLLOW_SELF(HttpStatus.BAD_REQUEST,"자기 자신을 팔로우 할 수 없습니다"),
    ALREADY_FOLLOWING(HttpStatus.CONFLICT,"팔로우를 이미 하고 있습니다!"),
    INVALID_UNFOLLOW_SELF(HttpStatus.BAD_REQUEST,"자기 자신을 팔로우 할 수 없습니다"),
    NOT_FOLLOWING(HttpStatus.BAD_REQUEST,"팔로우 중이 아닙니다."),

    // lock 관련 에러
    LOCK_TIMEOUT(HttpStatus.REQUEST_TIMEOUT, "시간 초과입니다.")

    ;
    private final HttpStatus status;// HTTP 상태 코드
    private final String message;// 에러 메시지
}
