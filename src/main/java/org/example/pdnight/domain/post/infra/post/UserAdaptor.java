package org.example.pdnight.domain.post.infra.post;
import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.post.application.PostUseCase.UserPort;
import org.example.pdnight.domain.user.domain.userDomain.UserReader;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UserAdaptor implements UserPort {

    private final UserReader userReader;

    @Override
    public List<Long> findFollowersOf(Long writerId) {
        return userReader.findFollowers(writerId);
    }
}
