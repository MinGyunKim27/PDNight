package org.example.pdnight.domain.auth.application.authUseCase;

import org.example.pdnight.domain.auth.presentation.dto.response.UserInfo;

public interface UserQueryPort {
    UserInfo getUserInfoById(Long userId);
}
