package org.example.pdnight.domain.auth.service;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.auth.dto.request.LoginRequestDto;
import org.example.pdnight.domain.auth.dto.request.SignupRequestDto;
import org.example.pdnight.domain.auth.dto.request.WithdrawRequestDto;
import org.example.pdnight.domain.auth.dto.response.LoginResponseDto;
import org.example.pdnight.domain.auth.dto.response.SignupResponseDto;
import org.example.pdnight.domain.common.enums.ErrorCode;
import org.example.pdnight.domain.common.exception.BaseException;
import org.example.pdnight.domain.hobby.entity.Hobby;
import org.example.pdnight.domain.hobby.entity.UserHobby;
import org.example.pdnight.domain.hobby.repository.HobbyRepositoryQuery;
import org.example.pdnight.domain.techStack.entity.TechStack;
import org.example.pdnight.domain.techStack.entity.UserTech;
import org.example.pdnight.domain.techStack.repository.TechStackRepositoryQuery;
import org.example.pdnight.domain.user.entity.User;
import org.example.pdnight.domain.user.repository.UserRepository;
import org.example.pdnight.global.config.PasswordEncoder;
import org.example.pdnight.global.utils.JwtUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final HobbyRepositoryQuery hobbyRepositoryQuery;
    private final TechStackRepositoryQuery techStackRepositoryQuery;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public SignupResponseDto signup(SignupRequestDto request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new BaseException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }
        // List<Hobby> / List<TechStack> 생성 : DB 에서 있는거만 가져오기
        List<Hobby> hobbyList = getHobbyList(request);
        List<TechStack> techStackList = getTechStackList(request);

        String encodePassword = passwordEncoder.encode(request.getPassword());
        User user = new User(request, encodePassword);

        // List<Hobby> -> Set<UserHobby>  /  List<TechStack> -> Set<UserTech>
        Set<UserHobby> userHobbies = getUserHobbySet(hobbyList, user);
        Set<UserTech> userTechs = getUserTechSet(techStackList, user);

        // user 저장 : 취미, 기술 스택 저장
        user.setHobbyAndTech(userHobbies, userTechs);
        User saveUser = userRepository.save(user);

        return SignupResponseDto.from(saveUser);
    }

    @Transactional
    public LoginResponseDto login(LoginRequestDto request) {
        User user = getUserByEmail(request);

        validateUser(user, request.getPassword());

        String token = jwtUtil.createToken(user.getId(), user.getRole(), user.getNickname());
        return LoginResponseDto.from(token);
    }

    public void logout(Long userId) {

    }

    @Transactional
    public void withdraw(Long userId, WithdrawRequestDto request) {
        User user = getUserById(userId);

        validateUser(user, request.getPassword());

        user.softDelete();
    }


    // ----------------------------------- HELPER 메서드 ------------------------------------------------------ //
    // get
    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));
    }

    private User getUserByEmail(LoginRequestDto request) {
        return userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));
    }

    // validate
    private void validateUser(User user, String password) {
        if (user.getIsDeleted()) {
            throw new BaseException(ErrorCode.USER_DEACTIVATED);
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BaseException(ErrorCode.INVALID_PASSWORD);
        }
    }

    // ----------- 중간 테이블 용 Helper 메서드 --------------------------//
    private List<Hobby> getHobbyList(SignupRequestDto request) {
        List<Hobby> hobbyList = new ArrayList<>();
        if (request.getHobbyIdList() != null && !request.getHobbyIdList().isEmpty()) {
            hobbyList = hobbyRepositoryQuery.findByIdList(request.getHobbyIdList());
        }
        return hobbyList;
    }

    private List<TechStack> getTechStackList(SignupRequestDto request) {
        List<TechStack> techStackList = new ArrayList<>();
        if (request.getTechStackIdList() != null && !request.getTechStackIdList().isEmpty()) {
            techStackList = techStackRepositoryQuery.findByIdList(request.getTechStackIdList());
        }
        return techStackList;
    }

    private Set<UserHobby> getUserHobbySet(List<Hobby> hobbyList, User user) {
        return hobbyList.stream()
                .map(hobby -> new UserHobby(user, hobby))
                .collect(Collectors.toSet());
    }

    private Set<UserTech> getUserTechSet(List<TechStack> techStackList, User user) {
        return techStackList.stream()
                .map(techStack -> new UserTech(user, techStack))
                .collect(Collectors.toSet());
    }

}
