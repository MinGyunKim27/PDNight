package org.example.pdnight.domain.hobby.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.util.StringUtils;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.hobby.dto.response.HobbyResponse;
import org.example.pdnight.domain.hobby.entity.Hobby;
import org.example.pdnight.domain.hobby.entity.QHobby;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class HobbyRepositoryQueryImpl implements HobbyRepositoryQuery{
    private final JPAQueryFactory queryFactory;

    public List<Hobby> searchHobby(String hobbyString){
        QHobby hobby = QHobby.hobby1;

        BooleanBuilder booleanBuilder = new BooleanBuilder();

        if (StringUtils.isNullOrEmpty(hobbyString)){
            booleanBuilder.and(hobby.hobby.contains(hobbyString));
        }

        return queryFactory.selectFrom(hobby).where(booleanBuilder).fetch();
    }
}
