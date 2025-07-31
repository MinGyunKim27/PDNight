package org.example.pdnight.domain.user.infra.techStackInfra;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.user.domain.entity.QTechStack;
import org.example.pdnight.domain.user.domain.entity.TechStack;
import org.example.pdnight.domain.user.domain.teckStackDomain.TechStackReader;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class TechStackQueryRepositoryImpl implements TechStackReader {

    private final JPAQueryFactory queryFactory;

    QTechStack qTechStack = QTechStack.techStack1;

    @Override
    public List<TechStack> searchTechStack(String techStack) {
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
        return queryFactory.selectFrom(qTechStack)
                .where(qTechStack.id.in(ids))
                .fetch();
    }

    @Override
    public boolean existsTechStackByTechStack(String techStack) {

        Integer result = queryFactory
                .selectOne()
                .from(qTechStack)
                .where(qTechStack.techStack.eq(techStack))
                .fetchFirst();
        return result != null;
    }

    @Override
    public TechStack findByTechStack(String techStack) {
        QTechStack qTechStack = QTechStack.techStack1;

        return queryFactory
                .selectFrom(qTechStack)
                .where(qTechStack.techStack.eq(techStack))
                .fetchOne();
    }

    @Override
    public List<String> getNamesByIds(List<Long> techIds) {

        if (techIds == null || techIds.isEmpty()) return List.of();

        return queryFactory
                .select(qTechStack.techStack)
                .from(qTechStack)
                .where(qTechStack.id.in(techIds))
                .fetch();
    }

    @Override
    public Map<Long, String> getNamesByIdsMap(Set<Long> techIds) {
        if (techIds == null || techIds.isEmpty()) return Map.of();

        return queryFactory
                .selectFrom(qTechStack)
                .where(qTechStack.id.in(techIds))
                .fetch()
                .stream()
                .collect(Collectors.toMap(TechStack::getId, TechStack::getTechStack));
    }

}
