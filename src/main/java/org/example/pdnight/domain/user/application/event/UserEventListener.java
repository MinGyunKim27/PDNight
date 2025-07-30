package org.example.pdnight.domain.user.application.event;

import org.example.pdnight.domain.user.domain.entity.User;
import org.example.pdnight.domain.user.domain.userDomain.UserCommandQuery;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserEventListener {

    private final UserCommandQuery userRepositoryQuery;

    @Transactional
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public User handlerUserRegisteredEvent(UserRegisteredEvent event) {
        try {
            User user = User.fromUserEvent(event.getAuthId(), event.getRequest());
            return userRepositoryQuery.save(user);
        } catch (Exception e) {
            log.info("프로필 생성 실패: userId={}, error={}", event.getAuthId(), e.getMessage());
            return null;
        }
    }
}
