package org.example.pdnight.domain.post.infra.tag;

import org.springframework.data.jpa.repository.JpaRepository;
import org.example.pdnight.domain.post.domain.tag.Tag;

public interface TagJpaRepository extends JpaRepository<Tag, Long> {
    boolean existsTagByName(final String name);
}
