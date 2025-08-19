package org.example.pdnight.domain.user.infra.userInfra;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.user.domain.entity.QFollow;
import org.example.pdnight.domain.user.domain.entity.QUser;
import org.example.pdnight.domain.user.domain.entity.User;
import org.example.pdnight.domain.user.domain.entity.UserCoupon;
import org.example.pdnight.domain.user.domain.userDomain.UserReader;
import org.example.pdnight.domain.user.presentation.dto.userDto.response.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.example.pdnight.domain.user.domain.entity.QHobby.hobby1;
import static org.example.pdnight.domain.user.domain.entity.QTechStack.techStack1;
import static org.example.pdnight.domain.user.domain.entity.QUserCoupon.userCoupon;
import static org.example.pdnight.domain.user.domain.entity.QUserHobby.userHobby;
import static org.example.pdnight.domain.user.domain.entity.QUserTech.userTech;

@Repository
@RequiredArgsConstructor
public class UserReaderImpl implements UserReader {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<User> findById(Long id) {
        QUser user = QUser.user;

        return queryFactory
                .selectFrom(user)
                .where(user.id.eq(id))
                .stream()
                .findFirst();
    }

    @Override
    public Optional<User> findByIdWithFollow(Long id) {
        QUser user = QUser.user;

        return queryFactory
                .selectFrom(user)
                .leftJoin(user.followedOther)
                .where(user.id.eq(id))
                .distinct()
                .stream()
                .findFirst();
    }

    @Override
    public Page<UserResponse> searchUsers(String search, Pageable pageable) {
        QUser user = QUser.user;

        // 검색 조건
        BooleanBuilder builder = new BooleanBuilder()
                .and(user.name.containsIgnoreCase(search)
                        .or(user.nickname.containsIgnoreCase(search)));

        // user 조회
        List<UserResponse> contents = queryFactory
                .select(new QUserResponse(
                        user.id,
                        user.name,
                        user.nickname,
                        user.gender,
                        user.age,
                        user.jobCategory,
                        user.region,
                        user.workLocation,
                        user.comment,
                        user.createdAt,
                        user.updatedAt
                ))
                .from(user)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // user 조회 결과에 연관관계 필드 추가 : 취미, 기술스택
        mappingToDtoListWithList(contents);

        // Count 쿼리
        Long count = Optional.ofNullable(
                queryFactory
                        .select(user.count())
                        .from(user)
                        .where(builder)
                        .fetchOne()
        ).orElse(0L);

        return PageableExecutionUtils.getPage(contents, pageable, () -> count);
    }

    @Override
    public Page<UserResponse> findAll(Pageable pageable) {
        QUser user = QUser.user;

        // user 조회
        List<UserResponse> contents = queryFactory
                .select(new QUserResponse(
                        user.id,
                        user.name,
                        user.nickname,
                        user.gender,
                        user.age,
                        user.jobCategory,
                        user.region,
                        user.workLocation,
                        user.comment,
                        user.createdAt,
                        user.updatedAt
                ))
                .from(user)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // user 조회 결과에 연관관계 필드 추가 : 취미, 기술스택
        mappingToDtoListWithList(contents);

        // Count 쿼리
        Long count = Optional.ofNullable(
                queryFactory
                        .select(user.countDistinct())
                        .from(user)
                        .fetchOne()
        ).orElse(0L);

        return PageableExecutionUtils.getPage(contents, pageable, () -> count);
    }

    // List 형으로 추가 정보가 필요할 때 Dto 매핑 : 목록 조회 시
    private void mappingToDtoListWithList(List<UserResponse> users) {
        if (users == null || users.isEmpty()) return;

        // user ID 리스트 추출
        List<Long> userIds = users
                .stream()
                .map(UserResponse::getId)
                .toList();

        // 취미 맵 조회
        Map<Long, List<String>> hobbyMap = queryFactory
                .select(userHobby.user.id, hobby1.hobby)
                .from(userHobby)
                .join(hobby1).on(userHobby.hobbyId.eq(hobby1.id))
                .where(userHobby.user.id.in(userIds))
                .fetch()
                .stream()
                .collect(Collectors.groupingBy(
                        tuple -> tuple.get(userHobby.user.id),
                        Collectors.mapping(tuple -> tuple.get(hobby1.hobby), Collectors.toList())
                ));

        // 기술스택 맵 조회
        Map<Long, List<String>> techStackMap = queryFactory
                .select(userTech.user.id, techStack1.techStack)
                .from(userTech)
                .join(techStack1).on(userTech.techStackId.eq(techStack1.id))
                .where(userTech.user.id.in(userIds))
                .fetch()
                .stream()
                .collect(Collectors.groupingBy(
                        tuple -> tuple.get(userTech.user.id),
                        Collectors.mapping(tuple -> tuple.get(techStack1.techStack), Collectors.toList())
                ));

        // 각 DTO에 매핑
        for (UserResponse dto : users) {
            List<String> hobbyList = hobbyMap.getOrDefault(dto.getId(), Collections.emptyList());
            List<String> techList = techStackMap.getOrDefault(dto.getId(), Collections.emptyList());
            dto.setHobbyAndTech(hobbyList, techList);
        }
    }

    @Override
    public Page<FollowingResponse> findFollowingsByUserId(Long userId, Pageable pageable) {
        QFollow follow = QFollow.follow;
        QUser user = QUser.user;

        JPQLQuery<FollowingResponse> query = queryFactory
                .select(new QFollowingResponse(
                        follow.following.id,
                        follow.following.nickname
                ))
                .from(follow)
                .join(follow.following, user)
                .where(follow.follower.id.eq(userId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        JPQLQuery<Long> countQuery = queryFactory
                .select(follow.count())
                .from(follow)
                .where(follow.follower.id.eq(userId));

        return PageableExecutionUtils.getPage(query.fetch(), pageable, countQuery::fetchOne);
    }


    @Override
    public Page<UserCouponResponse> findUserCoupons(Long userId, LocalDateTime now, Pageable pageable) {
        List<UserCouponResponse> couponList = queryFactory
                .select(Projections.constructor(UserCouponResponse.class,
                        userCoupon))
                .from(userCoupon)
                .where(
                        userCoupon.user.id.eq(userId),
                        userCoupon.isUsed.isFalse(),
                        userCoupon.deadlineAt.isNull()
                                .or(userCoupon.deadlineAt.after(now))
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPQLQuery<Long> total = queryFactory
                .select(userCoupon.count())
                .from(userCoupon)
                .where(
                        userCoupon.user.id.eq(userId),
                        userCoupon.isUsed.isFalse(),
                        userCoupon.deadlineAt.isNull()
                                .or(userCoupon.deadlineAt.after(now))
                );

        return PageableExecutionUtils.getPage(couponList, pageable, total::fetchOne);
    }

    @Override
    public Optional<UserCoupon> findUserCoupon(Long userId, Long couponId, LocalDateTime now) {
        return queryFactory
                .selectFrom(userCoupon)
                .where(
                        userCoupon.user.id.eq(userId),
                        userCoupon.id.eq(couponId),
                        userCoupon.isUsed.isFalse(),
                        userCoupon.deadlineAt.isNull()
                                .or(userCoupon.deadlineAt.after(now))
                )
                .stream()
                .findFirst();
    }

    /**
     * 나를 팔로우 하는 사람들 아이디 조회하는 메서드
     *
     * @param userId 내 id
     * @return 나를 팔로우 하는 사람들 아이디들
     */
    @Override
    public List<Long> findFollowers(Long userId) {
        QFollow follow = QFollow.follow;

        return queryFactory
                .select(follow.follower.id)
                .from(follow)
                .where(follow.following.id.eq(userId))
                .fetch();
    }

    @Override
    public List<UserCoupon> findByDeadlineAtBetween(LocalDateTime start, LocalDateTime end) {
        return queryFactory
                .selectFrom(userCoupon)
                .where(
                        userCoupon.deadlineAt.between(start, end)
                )
                .fetch();
    }
}
