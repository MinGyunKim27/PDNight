package org.example.pdnight.domain1.follow.repository;

import org.example.pdnight.domain1.follow.dto.response.FollowingResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FollowRepositoryQuery {
    Page<FollowingResponseDto> findFollowingsByUserId(Long userId, Pageable pageable);
}
