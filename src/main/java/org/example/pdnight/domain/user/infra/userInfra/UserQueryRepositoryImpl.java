package org.example.pdnight.domain.user.infra.userInfra;

import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.user.domain.entity.*;
import org.example.pdnight.domain.user.domain.userDomain.UserReader;
import org.example.pdnight.domain.user.presentation.dto.userDto.response.FollowingResponseDto;
import org.example.pdnight.domain.user.presentation.dto.userDto.response.QFollowingResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserQueryRepositoryImpl implements UserReader {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<User> findById(Long id) {
        QUser user = QUser.user;

        return queryFactory
                .select(user)
                .where(user.id.eq(id))
                .stream()
                .findFirst();
    }

    @Override
    public Page<User> searchUsers(String search, Pageable pageable) {
        QUser user = QUser.user;

        List<User> content = queryFactory
                .selectFrom(user)
                .where(
                        user.name.containsIgnoreCase(search)
                                .or(user.nickname.containsIgnoreCase(search)
                                        .or(user.name.containsIgnoreCase(search)))
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // Count 쿼리
        JPAQuery<Long> countQuery = queryFactory
                .select(user.count())
                .from(user)
                .where(
                        user.name.containsIgnoreCase(search)
                                .or(user.nickname.containsIgnoreCase(search))
                );

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    @Override
    public Optional<User> findByIdAndIsDeletedFalse(Long id){

        QUser user = QUser.user;

        return queryFactory
                .select(user)
                .where(user.id.eq(id).and(user.isDeleted.eq(false)))
                .stream().findFirst();
    }

    @Override
    public Optional<User> findByEmailIsDeletedFalse(String email){
        QUser user = QUser.user;

        return queryFactory
                .select(user)
                .where(user.email.eq(email).and(user.isDeleted.eq(false)))
                .stream().findFirst();
    }

    @Override
    public Optional<User> findByIdWithInfoIsDeletedFalse(Long id){

        QUser user = QUser.user;
        QTechStack techStack = QTechStack.techStack1;
        QUserTech userTech = QUserTech.userTech;

        QHobby hobby = QHobby.hobby1;
        QUserHobby userHobby = QUserHobby.userHobby;

        return queryFactory
                .select(user)
                .where(user.id.eq(id).and(user.isDeleted.eq(false)))
                .stream().findFirst();
    }

    @Override
    public boolean existsByEmail(String email){
        QUser user = QUser.user;

        Integer result = queryFactory
                .selectOne()
                .from(user)
                .where(user.email.eq(email))
                .fetchFirst();

        return result != null;
    }

    @Override
    public Page<User> findAll(Pageable pageable) {
        QUser user = QUser.user;

        List<User> users = queryFactory
                .select(user)
                .from(user)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long count = queryFactory
                .select(user.count())
                .from(user)
                .fetchOne();


        return PageableExecutionUtils.getPage(users, pageable, () -> Optional.ofNullable(count).orElse(0L));
    }

    @Override
    public Page<FollowingResponseDto> findFollowingsByUserId(Long userId, Pageable pageable) {
        QFollow follow = QFollow.follow;
        QUser user = QUser.user;

        JPQLQuery<FollowingResponseDto> query = queryFactory
                .select(new QFollowingResponseDto(
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
}
