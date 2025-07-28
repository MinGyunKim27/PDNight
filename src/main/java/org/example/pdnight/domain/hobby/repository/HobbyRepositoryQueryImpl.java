package org.example.pdnight.domain.hobby.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.hobby.entity.Hobby;
import org.example.pdnight.domain.hobby.entity.QHobby;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class HobbyRepositoryQueryImpl implements HobbyRepositoryQuery {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Hobby> searchHobby(String hobby) {
        QHobby qHobby = QHobby.hobby1;
        BooleanBuilder booleanBuilder = new BooleanBuilder();

        if (StringUtils.hasText(hobby)) {
            booleanBuilder.and(qHobby.hobby.contains(hobby));
        }

        return queryFactory.selectFrom(qHobby)
                .where(booleanBuilder)
                .fetch();
    }

    @Override
    public List<Hobby> findByIdList(List<Long> ids) {
        QHobby hobby = QHobby.hobby1;
        return queryFactory.selectFrom(hobby)
                .where(hobby.id.in(ids))
                .fetch();
    }

}
