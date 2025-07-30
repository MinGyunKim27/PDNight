package org.example.pdnight.domain.user.application.userUseCase;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.common.enums.ErrorCode;
import org.example.pdnight.domain.common.exception.BaseException;
import org.example.pdnight.domain.user.domain.entity.Follow;
import org.example.pdnight.domain.user.domain.entity.User;
import org.example.pdnight.domain.user.domain.userDomain.UserCommandQuery;
import org.example.pdnight.domain.user.domain.userDomain.UserReader;
import org.example.pdnight.domain.user.presentation.dto.userDto.request.UserNicknameUpdateDto;
import org.example.pdnight.domain.user.presentation.dto.userDto.request.UserUpdateRequest;
import org.example.pdnight.domain.user.presentation.dto.userDto.response.FollowResponseDto;
import org.example.pdnight.domain.user.presentation.dto.userDto.response.UserResponseDto;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.example.pdnight.domain.common.enums.ErrorCode.*;

@Component
@RequiredArgsConstructor
public class UserCommandService {

    private final UserReader userReader;
    private final UserCommandQuery userCommandQuery;

    @Transactional
    public UserResponseDto updateMyProfile(Long userId, UserUpdateRequest request) {
        User user = getUserById(userId);

        // 수정 로직
        user.updateProfile(request.getName(),
                request.getNickname(),
                request.getGender(),
                request.getAge(),
                request.getJobCategory(),
                request.getRegion(),
                request.getComment(),
                request.getHobbyIdList(),
                request.getTechStackIdList() );

        //todo: 여기서 hobby, tech 호출, 중간 테이블에 기록
        userCommandQuery.save(user);

        return UserResponseDto.from(user);
    }

    public UserResponseDto updateNickname(Long userId, UserNicknameUpdateDto dto){
        User user = getUserById(userId);

        user.updateNickname(dto.getNickname());

        userCommandQuery.save(user);

        return UserResponseDto.from(user);
    }

    public void delete(Long userId) {
        User user = getUserById(userId);
        user.softDelete();
    }

    //팔로우
    public FollowResponseDto follow(Long userId, Long loginId) {

        User follower = getUserById(loginId);
        User following = getUserById(userId);
        // 자기 자신 팔로우 방지
        follower.validateIsSelfFollow(following,INVALID_FOLLOW_SELF);

        // 중복 팔로우 방지
        follower.validateExistFollowing(following);

        Follow follow = Follow.create(follower, following);

        return FollowResponseDto.from(follow);
    }

    //언팔로우
    @Transactional
    public void unfollow(Long userId, Long loginId) {

        User follower = getUserById(loginId);
        User following = getUserById(userId);

        // 자기 자신 언팔 방지
        follower.validateIsSelfFollow(following, INVALID_UNFOLLOW_SELF);
        //팔로우 중이 아님
        follower.validateIsNotFollowing(following, NOT_FOLLOWING);

        follower.unfollow(following);
    }


    // --------------------------------------------------------------------------------------------------------//
    // --------------------------------------------------------------------------------------------------------//
    // ----------------------------------- HELPER 메서드 ------------------------------------------------------ //
    // --------------------------------------------------------------------------------------------------------//
    // --------------------------------------------------------------------------------------------------------//

    // get
    private User getUserById(Long id) {
        return userReader.findById(id).orElseThrow(
                () -> new BaseException(ErrorCode.USER_NOT_FOUND));
    }

//    private Set<UserTech> getUserTechByIdList(List<Long> ids, User user) {
//        Set<UserTech> userTechs = new HashSet<>();
//
//        if (ids != null && !ids.isEmpty()) {
//            userTechs = techStackRepositoryQuery.findByIdList(ids)
//                    .stream()
//                    .map(techStack -> UserTech.create(user, techStack))
//                    .collect(Collectors.toSet());
//        }
//        return userTechs;
//    }
//
//    private Set<UserHobby> getUserHobbyByIdList(List<Long> ids, User user) {
//        Set<UserHobby> userHobbies = new HashSet<>();
//
//        if (ids != null && !ids.isEmpty()) {
//            userHobbies = hobbyRepositoryQuery.findByIdList(ids)
//                    .stream()
//                    .map(hobby -> UserHobby.create(user, hobby))
//                    .collect(Collectors.toSet());
//        }
//        return userHobbies;
//    }
}
