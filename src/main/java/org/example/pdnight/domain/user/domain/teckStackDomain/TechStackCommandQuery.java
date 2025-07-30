package org.example.pdnight.domain.user.domain.teckStackDomain;

import org.example.pdnight.domain.user.domain.entity.TechStack;
import org.springframework.stereotype.Component;

@Component
public interface TechStackCommandQuery {
    TechStack save(TechStack techStack);

    void delete(TechStack techStack);
}
