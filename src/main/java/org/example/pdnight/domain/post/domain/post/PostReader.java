package org.example.pdnight.domain.post.domain.post;

import org.example.pdnight.domain1.common.enums.JoinStatus;
import org.example.pdnight.domain1.participant.entity.PostParticipant;
import org.example.pdnight.domain1.post.dto.response.PostResponseWithApplyStatusDto;
import org.example.pdnight.domain1.post.dto.response.PostWithJoinStatusAndAppliedAtResponseDto;
import org.example.pdnight.domain1.post.entity.Post;
import org.example.pdnight.domain1.post.enums.PostStatus;
import org.example.pdnight.domain1.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface PostReader {

    Page<PostParticipant> findByPostAndStatus(Post post, JoinStatus joinStatus, Pageable pageable);

    PostResponseWithApplyStatusDto getOpenedPostById(Long id);

    Page<PostResponseWithApplyStatusDto> getMyLikePost(Long userId, Pageable pageable);

    Page<PostWithJoinStatusAndAppliedAtResponseDto> getConfirmedPost(Long userId, JoinStatus joinStatus, Pageable pageable);

    Page<PostResponseWithApplyStatusDto> getWrittenPost(Long userId, Pageable pageable);

    Page<PostResponseWithApplyStatusDto> getSuggestedPost(Long userId, Pageable pageable);

    Optional<Post> findById(Long postId);

    Optional<Post> findByIdAndStatus(Long postId, PostStatus postStatus);

    List<PostParticipant> findByUserAndPost(User user, Post post);

}
