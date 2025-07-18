package org.example.pdnight.domain.hobby.repository;

import org.example.pdnight.domain.hobby.entity.Hobby;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HobbyRepositoryQuery {
    List<Hobby> searchHobby(String searchHobby);
}
