package org.example.pdnight.domain.user.repository;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.user.entity.QUser;
import org.example.pdnight.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserRepositoryQueryImpl implements UserRepositoryQuery {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<User> searchUsers(String search, Pageable pageable) {
        QUser user = QUser.user;

        List<User> content = queryFactory
                .selectFrom(user)
                .where(
                        user.name.containsIgnoreCase(search)
                                .or(user.nickname.containsIgnoreCase(search)
                                        .or(user.email.containsIgnoreCase(search)))
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

}
