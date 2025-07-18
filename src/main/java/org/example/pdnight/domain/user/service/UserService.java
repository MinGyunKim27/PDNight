package org.example.pdnight.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.common.dto.PagedResponse;
import org.example.pdnight.domain.participant.enums.JoinStatus;
import org.example.pdnight.domain.post.dto.response.PostResponseDto;
import org.example.pdnight.domain.post.entity.Post;
import org.example.pdnight.domain.post.repository.PostRepositoryQuery;
import org.example.pdnight.domain.user.dto.response.PostWithJoinStatusAndAppliedAtResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final PostRepositoryQuery postRepositoryQuery;

    public PagedResponse<PostResponseDto> findMyLikedPosts(Long userId, Pageable pageable){
        Page<Post> myLikePost = postRepositoryQuery.getMyLikePost(userId, pageable);
        Page<PostResponseDto> postResponseDtos = myLikePost.map(PostResponseDto::toDto);
        return PagedResponse.from(postResponseDtos);
    }

    public PagedResponse<PostWithJoinStatusAndAppliedAtResponseDto> findMyConfirmedPosts(Long userId, JoinStatus joinStatus, Pageable pageable){
        Page<PostWithJoinStatusAndAppliedAtResponseDto> myLikePost = postRepositoryQuery.getConfirmedPost(userId,joinStatus, pageable);
        return PagedResponse.from(myLikePost);
    }
}
