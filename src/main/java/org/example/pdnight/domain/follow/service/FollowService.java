package org.example.pdnight.domain.follow.service;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.common.enums.ErrorCode;
import org.example.pdnight.domain.common.exception.BaseException;
import org.example.pdnight.domain.follow.dto.response.FollowResponseDto;
import org.example.pdnight.domain.follow.dto.response.FollowingResponseDto;
import org.example.pdnight.domain.follow.entity.Follow;
import org.example.pdnight.domain.follow.repository.FollowRepository;
import org.example.pdnight.domain.follow.repository.FollowRepositoryQuery;
import org.example.pdnight.domain.user.entity.User;
import org.example.pdnight.domain.user.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.example.pdnight.domain.common.enums.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class FollowService {
    private final FollowRepository followRepository;
    private final FollowRepositoryQuery followRepositoryQuery;
    private final UserService userService;

    //팔로우
    public FollowResponseDto follow(Long userId, Long loggedInUserId) {
        if (userId.equals(loggedInUserId)) {
            throw new BaseException(INVALID_FOLLOW_SELF); // 자기 자신 팔로우 방지
        }

        User following = userService.getUserById(userId);
        User follower = userService.getUserById(loggedInUserId);

        boolean alreadyFollowing = followRepository.existsByFollowerAndFollowing(follower, following);
        if (alreadyFollowing) {
            throw new BaseException(ALREADY_FOLLOWING); // 중복 팔로우 방지
        }

        Follow follow = Follow.create(follower,following);
        followRepository.save(follow);

        return FollowResponseDto.toDto(follow);
    }

    //언팔로우
    @Transactional
    public void unfollow(Long userId, Long loggedInUserId) {
        if (userId.equals(loggedInUserId)) {
            throw new BaseException(INVALID_UNFOLLOW_SELF); // 자기 자신 언팔 방지
        }

        User following = userService.getUserById(userId);
        User follower = userService.getUserById(loggedInUserId);

        Follow follow = followRepository.findByFollowerAndFollowing(follower, following)
                .orElseThrow(() -> new BaseException(NOT_FOLLOWING)); //팔로우 중이 아님

        followRepository.delete(follow);
    }

    public Page<FollowingResponseDto> getFollowings(Long myId, Pageable pageable){
        return followRepositoryQuery.findFollowingsByUserId(myId,pageable);
    }


}
