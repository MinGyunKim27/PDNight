package org.example.pdnight.domain.post.application.PostUseCase;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface UserPort {
    List<Long> findFollowersOf(Long writerId);
}
