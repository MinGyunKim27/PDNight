package org.example.pdnight.domain.user.application.hobbyUseCase;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.user.domain.entity.Hobby;
import org.example.pdnight.domain.user.domain.hobbyDomain.HobbyReader;
import org.example.pdnight.domain.user.presentation.dto.hobbyDto.response.HobbyResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HobbyReaderService {

    private final HobbyReader hobbyReader;

    @Transactional(transactionManager = "transactionManager", readOnly = true)
    public List<HobbyResponse> searchHobby(String searchHobby) {
        List<Hobby> hobbies = hobbyReader.searchHobby(searchHobby);
        return hobbies.stream().map(HobbyResponse::from).toList();
    }
}
