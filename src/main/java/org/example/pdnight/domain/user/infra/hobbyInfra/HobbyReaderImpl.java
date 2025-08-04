package org.example.pdnight.domain.user.infra.hobbyInfra;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.user.domain.entity.Hobby;
import org.example.pdnight.domain.user.domain.entity.QHobby;
import org.example.pdnight.domain.user.domain.hobbyDomain.HobbyReader;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class HobbyReaderImpl implements HobbyReader {

    private final JPAQueryFactory queryFactory;

    QHobby qHobby = QHobby.hobby1;

    @Override
    public List<Hobby> searchHobby(String hobby) {
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
        return queryFactory.selectFrom(qHobby)
                .where(qHobby.id.in(ids))
                .fetch();
    }

    @Override
    public Boolean existsHobbiesByHobby(String hobbyName){

        Integer existOne = queryFactory
                .selectOne()
                .from(qHobby)
                .where(qHobby.hobby.eq(hobbyName))
                .fetchFirst();

        return existOne != null && existOne > 0;
    }

    @Override
    public Hobby findByhobby(String hobbyName){

        return  queryFactory
                .selectFrom(qHobby)
                .where(qHobby.hobby.eq(hobbyName))
                .fetchOne();
    }

    @Override
    public List<String> getNamesByIds(List<Long> hobbyIds) {

        if (hobbyIds == null || hobbyIds.isEmpty()) return List.of();

        return queryFactory
                .select(qHobby.hobby)
                .from(qHobby)
                .where(qHobby.id.in(hobbyIds))
                .fetch();
    }

    @Override
    public Map<Long, String> getNamesByIdsMap(Set<Long> hobbyIds) {

        if (hobbyIds == null || hobbyIds.isEmpty()) return Map.of();

        return queryFactory
                .selectFrom(qHobby)
                .where(qHobby.id.in(hobbyIds))
                .fetch()
                .stream()
                .collect(Collectors.toMap(Hobby::getId, Hobby::getHobby));
    }

}
