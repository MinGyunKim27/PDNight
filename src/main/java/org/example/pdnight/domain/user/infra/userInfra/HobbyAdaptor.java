package org.example.pdnight.domain.user.infra.userInfra;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.user.application.userUseCase.HobbyPort;
import org.example.pdnight.domain.user.domain.hobbyDomain.HobbyReader;
import org.example.pdnight.domain.user.domain.userDomain.UserCommander;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class HobbyAdaptor implements HobbyPort {
    private final HobbyReader hobbyReader;
    public List<String> getHobbies(List<Long> hobbyIds) {
        return hobbyReader.getNamesByIds(hobbyIds);
    }
}
