package org.example.pdnight.domain.user.repository;

import java.util.Optional;

import org.example.pdnight.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

import org.example.pdnight.domain.user.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	//포스트 도메인에 사용할 임시 메서드 유저도메인에 해당 메서드 추가시 삭제
	Optional<User> findByIdAndIsDeletedFalse(Long id);
}
