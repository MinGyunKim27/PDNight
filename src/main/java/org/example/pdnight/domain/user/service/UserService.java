package org.example.pdnight.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.user.dto.request.UserRequestDto;
import org.example.pdnight.domain.user.dto.request.UserUpdateRequest;
import org.example.pdnight.domain.user.dto.response.UserResponseDto;
import org.example.pdnight.domain.user.entity.User;
import org.example.pdnight.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserResponseDto getMyProfile(Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return new UserResponseDto(user);
    }

    @Transactional
    public UserResponseDto updateMyProfile(Long userId, UserUpdateRequest request){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 수정 로직
        user.updateProfile(request);
        userRepository.save(user);

        return new UserResponseDto(user);
    }
}
