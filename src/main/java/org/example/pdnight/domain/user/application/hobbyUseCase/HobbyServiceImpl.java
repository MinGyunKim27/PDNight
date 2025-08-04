package org.example.pdnight.domain.user.application.hobbyUseCase;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.user.presentation.dto.hobbyDto.request.HobbyRequest;
import org.example.pdnight.domain.user.presentation.dto.hobbyDto.response.HobbyResponse;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class HobbyServiceImpl implements HobbyService {

    private final HobbyCommanderService hobbyCommander;
    private final HobbyReaderService hobbyReader;

    @Override
    public HobbyResponse createHobby(HobbyRequest dto) {
        return hobbyCommander.createHobby(dto);
    }

    @Override
    public List<HobbyResponse> searchHobby(String searchHobby) {
        return hobbyReader.searchHobby(searchHobby);
    }
}
