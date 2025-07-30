package org.example.pdnight.domain1.techStack.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain1.techStack.entity.QTechStack;
import org.example.pdnight.domain1.techStack.entity.TechStack;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class TechStackRepositoryQueryImpl implements TechStackRepositoryQuery {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<TechStack> searchTechStack(String techStack) {
        QTechStack qTechStack = QTechStack.techStack1;
        BooleanBuilder booleanBuilder = new BooleanBuilder();

        if (StringUtils.hasText(techStack)) {
            booleanBuilder.and(qTechStack.techStack.contains(techStack));
        }

        return queryFactory.selectFrom(qTechStack)
                .where(booleanBuilder)
                .fetch();
    }

    @Override
    public List<TechStack> findByIdList(List<Long> ids) {
        QTechStack qTechStack = QTechStack.techStack1;
        return queryFactory.selectFrom(qTechStack)
                .where(qTechStack.id.in(ids))
                .fetch();
    }

}
