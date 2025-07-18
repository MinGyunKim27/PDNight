package org.example.pdnight.domain.post.repository;

import org.example.pdnight.domain.participant.enums.JoinStatus;
import org.example.pdnight.domain.post.dto.response.PostResponseDto;
import org.example.pdnight.domain.post.entity.Post;
import org.example.pdnight.domain.user.dto.response.PostWithJoinStatusAndAppliedAtResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepositoryQuery {
    Page<Post> getMyLikePost(Long userId, Pageable pageable);

    Page<PostWithJoinStatusAndAppliedAtResponseDto> getConfirmedPost(Long userId, JoinStatus joinStatus, Pageable pageable);
}
