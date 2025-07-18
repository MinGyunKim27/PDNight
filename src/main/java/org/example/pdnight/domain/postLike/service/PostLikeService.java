package org.example.pdnight.domain.postLike.service;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.common.enums.ErrorCode;
import org.example.pdnight.domain.common.exception.BaseException;
import org.example.pdnight.domain.post.entity.Post;
import org.example.pdnight.domain.post.repository.PostRepository;
import org.example.pdnight.domain.postLike.dto.response.PostLikeResponse;
import org.example.pdnight.domain.postLike.entity.PostLike;
import org.example.pdnight.domain.postLike.repository.PostLikeRepository;
import org.example.pdnight.domain.user.entity.User;
import org.example.pdnight.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostLikeService {
    private final PostLikeRepository postLikeRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    @Transactional
    public PostLikeResponse addLike(Long id, Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));
        Post post = postRepository.findById(id)
                .orElseThrow(()-> new BaseException(ErrorCode.POST_NOT_FOUND));

        if (postLikeRepository.existsByPostAndUser(post, user)) {
            throw new BaseException(ErrorCode.ALREADY_LIKED);
        }

        PostLike postLike = new PostLike(post, user);
        postLikeRepository.save(postLike);

        return new PostLikeResponse(postLike);
    }

    @Transactional
    public void removeLike(Long id, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));
        Post post = postRepository.findById(id)
                .orElseThrow(()-> new BaseException(ErrorCode.POST_NOT_FOUND));
        PostLike like = postLikeRepository.findByPostAndUser(post, user)
                .orElseThrow(()-> new BaseException(ErrorCode.POSTLIKE_NOT_FOUND));

        postLikeRepository.delete(like);
    }
}
