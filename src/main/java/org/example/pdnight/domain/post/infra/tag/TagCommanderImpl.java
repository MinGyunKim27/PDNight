package org.example.pdnight.domain.post.infra.tag;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.post.domain.tag.TagCommander;
import org.springframework.stereotype.Component;
import org.example.pdnight.domain.post.domain.tag.Tag;

@Component
@RequiredArgsConstructor
public class TagCommanderImpl implements TagCommander {
    private final TagJpaRepository tagJpaRepository;

    public Tag save(Tag tag){
        return tagJpaRepository.save(tag);
    }

    @Override
    public boolean existsTagByName(String tagName) {
        return  tagJpaRepository.existsTagByName(tagName);
    }
}
