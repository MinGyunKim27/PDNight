package org.example.pdnight.domain.user.infra.hobbyInfra;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.user.domain.entity.Hobby;
import org.example.pdnight.domain.user.domain.hobbyDomain.HobbyReader;
import org.example.pdnight.domain.user.domain.entity.QHobby;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class HobbyQueryRepositoryImpl implements HobbyReader {

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

    public Boolean existsHobbiesByHobby(String hobbyName){
        QHobby hobby = QHobby.hobby1;

        Integer existOne = queryFactory
                .selectOne()
                .from(hobby)
                .where(hobby.hobby.eq(hobbyName))
                .fetchFirst();

        return existOne != null && existOne > 0;
    }

    public Hobby findByhobby(String hobbyName){
        QHobby hobby = QHobby.hobby1;

        return  queryFactory
                .selectFrom(hobby)
                .where(hobby.hobby.eq(hobbyName))
                .fetchOne();
    }
}
