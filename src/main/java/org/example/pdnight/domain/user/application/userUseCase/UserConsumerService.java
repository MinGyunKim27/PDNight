package org.example.pdnight.domain.user.application.userUseCase;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.user.domain.entity.Follow;
import org.example.pdnight.domain.user.domain.entity.User;
import org.example.pdnight.domain.user.domain.userDomain.UserCommander;
import org.example.pdnight.global.common.enums.ErrorCode;
import org.example.pdnight.global.common.exception.BaseException;
import org.example.pdnight.global.event.AuthDeletedEvent;
import org.example.pdnight.global.event.AuthSignedUpEvent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserConsumerService {
    private final UserCommander userCommander;

    @Transactional
    public void consumeAuthSignedUpEvent(AuthSignedUpEvent event) {

        User user = User.fromAuthSignUpEvent(event.name(), event.nickname(), event.gender(), event.age(), event.jobCategory());
        userCommander.save(user);
    }

    @Transactional
    public void handleAuthDelete(AuthDeletedEvent event) {
        User user = userCommander.findById(event.authId())
                .orElseThrow(()-> new BaseException(ErrorCode.USER_NOT_FOUND));

        if (user.getIsDeleted()) {
            throw new BaseException(ErrorCode.USER_DEACTIVATED);
        }

        List<Follow> followingMe = user.getFollowingMe();

        followingMe.clear();

        user.softDelete();
        // 유저 팔로우 삭제 기능 들어가야됨

    }
}
