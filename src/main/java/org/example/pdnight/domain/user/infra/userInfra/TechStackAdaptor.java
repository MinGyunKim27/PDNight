package org.example.pdnight.domain.user.infra.userInfra;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.user.application.userUseCase.HobbyPort;
import org.example.pdnight.domain.user.application.userUseCase.TechStackPort;
import org.example.pdnight.domain.user.domain.hobbyDomain.HobbyReader;
import org.example.pdnight.domain.user.domain.teckStackDomain.TechStackReader;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TechStackAdaptor implements TechStackPort {
    private final TechStackReader techStackReader;
    public List<String> getTechStackNames(List<Long> techStackIds) {
        return techStackReader.getNamesByIds(techStackIds);
    }
}
