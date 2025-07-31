package org.example.pdnight.domain.user.application.userUseCase.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.pdnight.domain.common.enums.ErrorCode;
import org.example.pdnight.domain.common.exception.BaseException;
import org.example.pdnight.domain.user.domain.entity.User;
import org.example.pdnight.domain.user.domain.userDomain.UserCommander;
import org.example.pdnight.domain.user.domain.userDomain.UserReader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserEventListener {

    private final UserCommander userCommandQuery;
    private final UserReader userReader;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public User handlerUserSignUpEvent(UserSignUpEvent event) {
        try {
            User user = User.fromUserSignUpEvent(event.getAuthId(), event.getRequest());
            return userCommandQuery.save(user);
        } catch (Exception e) {
            log.info("프로필 생성 실패: userId={}, error={}", event.getAuthId(), e.getMessage());
            return null;
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleUserDelete(UserDeleteEvent event) {
        try {
            User user = userReader.findById(event.getAuthId()).orElseThrow(
                    () -> new BaseException(ErrorCode.USER_NOT_FOUND)
            );
            user.softDelete();
        } catch (Exception e) {
            log.info("프로필 삭제 실패: userId={}, error={}", event.getAuthId(), e.getMessage());
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleUserEvaludation(UserEvaluationEvent event) {
        try {
            User user = userReader.findById(event.getUserId()).orElseThrow(
                    () -> new BaseException(ErrorCode.USER_NOT_FOUND)
            );
            user.updateEvaluation(event.getRate());
        } catch (Exception e) {
            log.info("평가 갱신 실패: userId={}, error={}", event.getUserId(), e.getMessage());
        }
    }
}
