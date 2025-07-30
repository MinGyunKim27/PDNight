package org.example.pdnight.domain.user.domain.userDomain;

import org.example.pdnight.domain.user.domain.entity.User;
import org.springframework.stereotype.Component;

@Component
public interface UserCommandQuery {

    User save(User user);

}
