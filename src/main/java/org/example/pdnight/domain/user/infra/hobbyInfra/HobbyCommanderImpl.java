package org.example.pdnight.domain.user.infra.hobbyInfra;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.user.domain.entity.Hobby;
import org.example.pdnight.domain.user.domain.hobbyDomain.HobbyCommander;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class HobbyCommanderImpl implements HobbyCommander {

    private final HobbyJpaRepository hobbyJpaRepository;

    @Override
    public Hobby save(Hobby hobby) {
        return hobbyJpaRepository.save(hobby);
    }
}
