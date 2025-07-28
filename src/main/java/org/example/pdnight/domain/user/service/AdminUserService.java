package org.example.pdnight.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.common.dto.PagedResponse;
import org.example.pdnight.domain.common.enums.ErrorCode;
import org.example.pdnight.domain.common.exception.BaseException;
import org.example.pdnight.domain.user.dto.request.UserNicknameUpdateDto;
import org.example.pdnight.domain.user.dto.response.UserResponseDto;
import org.example.pdnight.domain.user.entity.User;
import org.example.pdnight.domain.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public PagedResponse<UserResponseDto> getAllUsers(Pageable pageable) {

        Page<User> users = userRepository.findAll(pageable);

        return PagedResponse.from(users.map(UserResponseDto::from));
    }

    @Transactional
    public UserResponseDto updateNickname(Long userId, UserNicknameUpdateDto dto) {

        User user = getUserById(userId);

        user.updateNickname(dto.getNickname());

        return UserResponseDto.from(user);
    }

    @Transactional
    public void deleteUser(Long userId) {

        User user = getUserById(userId);

        validateIsDeleted(user);

        user.softDelete();
    }

    // ----------------------------------- HELPER 메서드 ------------------------------------------------------ //
    // get
    private User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new BaseException(ErrorCode.USER_NOT_FOUND));
    }

    // validate
    private void validateIsDeleted(User user){
        if(user.getIsDeleted()) {
            throw new BaseException(ErrorCode.USER_DEACTIVATED);
        }
    }
}
