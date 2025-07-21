package org.example.pdnight.domain.hobby.service;


import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.common.enums.ErrorCode;
import org.example.pdnight.domain.common.exception.BaseException;
import org.example.pdnight.domain.hobby.dto.request.HobbyRequest;
import org.example.pdnight.domain.hobby.dto.response.HobbyResponse;
import org.example.pdnight.domain.hobby.entity.Hobby;
import org.example.pdnight.domain.hobby.repository.HobbyRepository;
import org.example.pdnight.domain.hobby.repository.HobbyRepositoryQuery;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HobbyService {

    private final HobbyRepository hobbyRepository;
    private final HobbyRepositoryQuery hobbyRepositoryQuery;

    public HobbyResponse createHobby(HobbyRequest dto){
        Boolean exists = hobbyRepository.existsHobbiesByHobby(dto.getHobby());

        if (exists){
            throw new BaseException(ErrorCode.HOBBY_ALREADY_EXISTS);
        }

        Hobby hobby = new Hobby(dto.getHobby());

        Hobby save = hobbyRepository.save(hobby);
        return HobbyResponse.of(save);
    }

    public List<HobbyResponse> searchHobby(String searchHobby){
        List<Hobby> hobbies = hobbyRepositoryQuery.searchHobby(searchHobby);
        return hobbies.stream().map(HobbyResponse::of).toList();
    }
}
