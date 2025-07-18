package org.example.pdnight.domain.techStack.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.techStack.entity.QTechStack;
import org.example.pdnight.domain.techStack.entity.TechStack;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class TechStackRepositoryQueryImpl implements TechStackRepositoryQuery{

    private final JPAQueryFactory queryFactory;

    @Override
    public List<TechStack> searchTechStack(String techStack) {
        QTechStack qTechStack = QTechStack.techStack1;
        BooleanBuilder booleanBuilder = new BooleanBuilder();

        if (techStack!=null){
            booleanBuilder.and(qTechStack.techStack.contains(techStack));
        }

        return queryFactory
                .select(qTechStack)
                .from(qTechStack)
                .where(booleanBuilder)
                .fetch();
    }

}
