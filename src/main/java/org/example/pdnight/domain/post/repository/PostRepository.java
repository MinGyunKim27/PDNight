package org.example.pdnight.domain.post.repository;

import java.util.Optional;

import org.example.pdnight.domain.common.enums.JobCategory;
import org.example.pdnight.domain.post.dto.response.PostResponseDto;
import org.example.pdnight.domain.post.entity.Post;
import org.example.pdnight.domain.post.enums.AgeLimit;
import org.example.pdnight.domain.post.enums.Gender;
import org.example.pdnight.domain.post.enums.PostStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long>{
	//상태값 조건 쿼리메서드
	Optional<Post> findByIdAndStatus(Long id, PostStatus status);

}
