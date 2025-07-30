package org.example.pdnight.domain1.hobby.service;


import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain1.common.enums.ErrorCode;
import org.example.pdnight.domain1.common.exception.BaseException;


import org.example.pdnight.domain1.hobby.dto.request.HobbyRequest;
import org.example.pdnight.domain1.hobby.dto.response.HobbyResponse;
import org.example.pdnight.domain1.hobby.entity.Hobby;
import org.example.pdnight.domain1.hobby.repository.HobbyRepository;
import org.example.pdnight.domain1.hobby.repository.HobbyRepositoryQuery;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HobbyService {

    private final HobbyRepository hobbyRepository;
    private final HobbyRepositoryQuery hobbyRepositoryQuery;

    public HobbyResponse createHobby(HobbyRequest dto){
        validateExistHobby(dto.getHobby());

        Hobby hobby = Hobby.from(dto.getHobby());

        Hobby save = hobbyRepository.save(hobby);
        return HobbyResponse.from(save);
    }

    public List<HobbyResponse> searchHobby(String searchHobby){
        List<Hobby> hobbies = hobbyRepositoryQuery.searchHobby(searchHobby);
        return hobbies.stream().map(HobbyResponse::from).toList();
    }

    // ----------------------------------- HELPER 메서드 ------------------------------------------------------ //
    // validate
    private void validateExistHobby(String hobby) {
        Boolean exists = hobbyRepository.existsHobbiesByHobby(hobby);
        if (exists){
            throw new BaseException(ErrorCode.HOBBY_ALREADY_EXISTS);
        }
    }
}
