package org.example.pdnight.domain.user.repository;

import org.example.pdnight.domain.user.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    //포스트 도메인에 사용할 임시 메서드 유저도메인에 해당 메서드 추가시 삭제
    Optional<User> findByIdAndIsDeletedFalse(Long id);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    // 취미, 기술스택을 사용할 때
    @EntityGraph(attributePaths = {"userHobbies.hobby", "userTechs.techStack"})
    @Query("SELECT u FROM User u WHERE u.id = :id")
    Optional<User> findByIdWithInfo(Long id);

}
