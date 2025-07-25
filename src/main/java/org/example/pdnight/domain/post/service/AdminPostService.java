package org.example.pdnight.domain.post.service;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.common.enums.ErrorCode;
import org.example.pdnight.domain.common.exception.BaseException;
import org.example.pdnight.domain.post.entity.Post;
import org.example.pdnight.domain.post.repository.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminPostService {

    private final PostRepository postRepository;

    @Transactional
    public void deletePostById(Long id) {
        Post foundPost = getPostOrThrow(postRepository.findById(id));

        foundPost.unlinkReviews();
        postRepository.delete(foundPost);
    }

    // -- HELPER 메서드 -- //
    // get
    private Post getPostOrThrow(Optional<Post> post) {
        return post.orElseThrow(() -> new BaseException(ErrorCode.POST_NOT_FOUND));
    }
    // validate
}
