package org.example.pdnight.domain.post.domain.tag;

import org.springframework.stereotype.Component;

@Component
public interface TagCommander {
    Tag save(Tag tag);

    boolean existsTagByName(String tagName);
}
