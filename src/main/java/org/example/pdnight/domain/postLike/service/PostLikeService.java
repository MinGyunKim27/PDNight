package org.example.pdnight.domain.postLike.service;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.common.enums.ErrorCode;
import org.example.pdnight.domain.common.exception.BaseException;
import org.example.pdnight.domain.common.helper.GetHelper;
import org.example.pdnight.domain.post.entity.Post;
import org.example.pdnight.domain.postLike.dto.response.PostLikeResponse;
import org.example.pdnight.domain.postLike.entity.PostLike;
import org.example.pdnight.domain.postLike.repository.PostLikeRepository;
import org.example.pdnight.domain.user.entity.User;
import org.example.pdnight.global.constant.CacheName;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostLikeService {

    private final PostLikeRepository postLikeRepository;
    private final GetHelper helper;

    @CacheEvict(value = CacheName.LIKED_POST, allEntries = true)
    @Transactional
    public PostLikeResponse addLike(Long id, Long userId) {

        User user = helper.getUserByIdOrElseThrow(userId);
        Post post = helper.getPostByIdOrElseThrow(id);

        //좋아요 존재 하면 에러
        validateExists(post, user);

        PostLike postLike = PostLike.create(post, user);
        post.addLike(postLike);
        postLikeRepository.save(postLike);

        return PostLikeResponse.from(postLike);
    }

    @CacheEvict(value = CacheName.LIKED_POST, allEntries = true)
    @Transactional
    public void removeLike(Long id, Long userId) {

        User user = helper.getUserByIdOrElseThrow(userId);
        Post post = helper.getPostByIdOrElseThrow(id);
        PostLike like = getPostLikePostAndUser(post, user);

        post.removeLike(like);
        postLikeRepository.delete(like);
    }

    // -- HELPER 메서드 -- //
    // get
    public PostLike getPostLikePostAndUser(Post post, User user) {
        return postLikeRepository.findByPostAndUser(post, user)
                .orElseThrow(() -> new BaseException(ErrorCode.POSTLIKE_NOT_FOUND));
    }

    // validate
    public void validateExists(Post post, User user) {
        if (postLikeRepository.existsByPostAndUser(post, user)) {
            throw new BaseException(ErrorCode.ALREADY_LIKED);
        }
    }

}
