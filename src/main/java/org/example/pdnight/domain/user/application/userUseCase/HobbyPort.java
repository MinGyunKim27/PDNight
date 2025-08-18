package org.example.pdnight.domain.user.application.userUseCase;

import java.util.List;

public interface HobbyPort {
    List<String> getHobbies(List<Long> hobbyIds);
}
