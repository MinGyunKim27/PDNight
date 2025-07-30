package org.example.pdnight.domain1.post.service;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain1.comment.repository.CommentRepository;
import org.example.pdnight.domain1.common.enums.ErrorCode;
import org.example.pdnight.domain1.common.exception.BaseException;
import org.example.pdnight.domain1.post.entity.Post;
import org.example.pdnight.domain1.post.repository.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminPostService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public void deletePostById(Long id) {
        Post foundPost = getPostOrThrow(postRepository.findById(id));

        foundPost.unlinkReviews();
        commentRepository.deleteAllByChildrenPostId(id);
        commentRepository.deleteAllByPostId(id);
        postRepository.delete(foundPost);
    }

    // -- HELPER 메서드 -- //
    // get
    private Post getPostOrThrow(Optional<Post> post) {
        return post.orElseThrow(() -> new BaseException(ErrorCode.POST_NOT_FOUND));
    }
    // validate
}
