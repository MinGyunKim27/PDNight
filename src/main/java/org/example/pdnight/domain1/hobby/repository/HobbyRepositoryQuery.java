package org.example.pdnight.domain1.hobby.repository;

import org.example.pdnight.domain1.hobby.entity.Hobby;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HobbyRepositoryQuery {
    List<Hobby> searchHobby(String searchHobby);

    List<Hobby> findByIdList(List<Long> ids);
}
