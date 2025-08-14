package org.example.pdnight.domain.post.infra.tag;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.post.domain.tag.QTag;
import org.example.pdnight.domain.post.domain.tag.Tag;
import org.example.pdnight.domain.post.domain.tag.TagReader;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class TagReaderImpl implements TagReader {

    private final JPAQueryFactory queryFactory;

    QTag qTag = QTag.tag;

    @Override
    public List<Tag> searchTags(String tagName) {
        BooleanBuilder builder = new BooleanBuilder();

        if (StringUtils.hasText(tagName)) {
            builder.and(qTag.name.contains(tagName));
        }
        return queryFactory.selectFrom(qTag)
                .where(builder).fetch();
    }


    @Override
    public List<String> getNamesByIds(List<Long> tagIdList) {
        if (tagIdList == null || tagIdList.isEmpty()) return List.of();

        return queryFactory
                .select(qTag.name)
                .from(qTag)
                .where(qTag.id.in(tagIdList))
                .fetch();
    }

    @Override
    public Map<Long, String> getNamesByIdsMap(Set<Long> tagIdList) {
        if (tagIdList == null || tagIdList.isEmpty()) return Map.of();

        return queryFactory
                .selectFrom(qTag)
                .where(qTag.id.in(tagIdList))
                .fetch()
                .stream()
                .collect(Collectors.toMap(Tag::getId, Tag::getName));
    }
}
