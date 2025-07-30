package org.example.pdnight.domain.user.domain.hobbyDomain;

import org.example.pdnight.domain.user.domain.entity.Hobby;
import org.springframework.stereotype.Component;

@Component
public interface HobbyCommandQuery {
    void save(Hobby hobby);
}
