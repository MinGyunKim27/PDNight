package org.example.pdnight.domain.post.repository;

import org.example.pdnight.domain.common.enums.JobCategory;
import org.example.pdnight.domain.post.dto.response.PostResponseDto;
import org.example.pdnight.domain.post.enums.AgeLimit;
import org.example.pdnight.domain.post.enums.Gender;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepositoryQuery {

	//필터링 조건에 따른 동적쿼리 프로젝션으로 DTO 반환
	Page<PostResponseDto> findPostDtosBySearch(
		Pageable pageable,
		Integer maxParticipants,
		AgeLimit ageLimit,
		JobCategory jobCategoryLimit,
		Gender genderLimit
	);

}
