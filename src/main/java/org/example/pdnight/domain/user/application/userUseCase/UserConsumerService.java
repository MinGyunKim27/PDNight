package org.example.pdnight.domain.user.application.userUseCase;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.user.domain.entity.User;
import org.example.pdnight.domain.user.domain.userDomain.UserCommander;
import org.example.pdnight.global.common.enums.ErrorCode;
import org.example.pdnight.global.common.exception.BaseException;
import org.example.pdnight.global.event.AuthSignedUpEvent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserConsumerService {
    private final UserCommander userCommander;

    @Transactional
    public void consumeAuthSignedUpEvent(AuthSignedUpEvent event) {
        User user = userCommander.findById(event.authId())
                .orElseThrow(()-> new BaseException(ErrorCode.USER_NOT_FOUND));

        if (user.getIsDeleted()) {
            throw new BaseException(ErrorCode.USER_DEACTIVATED);
        }

        user.softDelete();
    }
}
