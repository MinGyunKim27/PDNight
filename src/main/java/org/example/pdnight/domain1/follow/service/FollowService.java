package org.example.pdnight.domain1.follow.service;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain1.common.enums.ErrorCode;
import org.example.pdnight.domain1.common.exception.BaseException;
import org.example.pdnight.domain1.common.helper.GetHelper;
import org.example.pdnight.domain1.follow.dto.response.FollowResponseDto;
import org.example.pdnight.domain1.follow.dto.response.FollowingResponseDto;
import org.example.pdnight.domain1.follow.entity.Follow;
import org.example.pdnight.domain1.follow.repository.FollowRepository;
import org.example.pdnight.domain1.follow.repository.FollowRepositoryQuery;
import org.example.pdnight.domain1.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.example.pdnight.domain1.common.enums.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository followRepository;
    private final FollowRepositoryQuery followRepositoryQuery;
    private final GetHelper helper;

    //팔로우
    public FollowResponseDto follow(Long userId, Long loginId) {
        // 자기 자신 팔로우 방지
        validateIsSelfFollow(userId, loginId, INVALID_FOLLOW_SELF);

        User following = helper.getUserByIdOrElseThrow(userId);
        User follower = helper.getUserByIdOrElseThrow(loginId);

        // 중복 팔로우 방지
        validateExistFollowing(follower, following);

        Follow follow = Follow.create(follower, following);
        followRepository.save(follow);

        return FollowResponseDto.from(follow);
    }

    //언팔로우
    @Transactional
    public void unfollow(Long userId, Long loginId) {
        // 자기 자신 언팔 방지
        validateIsSelfFollow(userId, loginId, INVALID_UNFOLLOW_SELF);

        User following = helper.getUserByIdOrElseThrow(userId);
        User follower = helper.getUserByIdOrElseThrow(loginId);

        //팔로우 중이 아님
        Follow follow = getFollow(follower, following);

        followRepository.delete(follow);
    }

    public Page<FollowingResponseDto> getFollowings(Long myId, Pageable pageable) {
        return followRepositoryQuery.findFollowingsByUserId(myId, pageable);
    }

    // ----------------------------------- HELPER 메서드 ------------------------------------------------------ //
    // get
    private Follow getFollow(User follower, User following) {
        return followRepository.findByFollowerAndFollowing(follower, following)
                .orElseThrow(() -> new BaseException(NOT_FOLLOWING));
    }
    // validate
    private void validateExistFollowing(User follower, User following) {
        boolean alreadyFollowing = followRepository.existsByFollowerAndFollowing(follower, following);
        if (alreadyFollowing) {
            throw new BaseException(ALREADY_FOLLOWING);
        }
    }

    private void validateIsSelfFollow(Long userId, Long loginId, ErrorCode invalidFollowSelf) {
        if (userId.equals(loginId)) {
            throw new BaseException(invalidFollowSelf);
        }
    }
}
