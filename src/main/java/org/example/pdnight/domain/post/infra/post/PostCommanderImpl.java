package org.example.pdnight.domain.post.infra.post;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.post.domain.post.Post;
import org.example.pdnight.domain.post.domain.post.PostCommander;
import org.example.pdnight.domain.post.enums.PostStatus;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PostCommanderImpl implements PostCommander {

    private final PostJpaRepository postJpaRepository;

    @Override
    public Optional<Post> findByIdAndStatus(Long id, PostStatus status) {
        return postJpaRepository.findByIdAndStatus(id, status);
    }

    @Override
    public boolean existsById(Long id) {
        return postJpaRepository.existsById(id, PostStatus.CLOSED);
    }

    @Override
    public Optional<Post> findById(Long id) {
        return postJpaRepository.findById(id);
    }

    @Override
    public void deletePost(Post post) {
        postJpaRepository.delete(post);
    }

    @Override
    public Post save(Post post) {
        return postJpaRepository.save(post);
    }

}