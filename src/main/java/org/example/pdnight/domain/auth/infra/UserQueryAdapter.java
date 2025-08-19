package org.example.pdnight.domain.auth.infra;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.auth.application.authUseCase.UserQueryPort;
import org.example.pdnight.domain.auth.presentation.dto.response.UserInfo;
import org.example.pdnight.domain.user.domain.entity.User;
import org.example.pdnight.domain.user.domain.userDomain.UserReader;
import org.example.pdnight.global.common.enums.ErrorCode;
import org.example.pdnight.global.common.exception.BaseException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserQueryAdapter implements UserQueryPort {
    private final UserReader userReader;

    public UserInfo getUserInfoById(Long id) {
        User user = userReader.findById(id).orElseThrow(
                () -> new BaseException(ErrorCode.USER_NOT_FOUND)
        );
        return UserInfo.from(
                user.getNickname(),
                user.getAge(),
                user.getGender(),
                user.getJobCategory()); // 필요한 정보만 담은 DTO
    }
}

