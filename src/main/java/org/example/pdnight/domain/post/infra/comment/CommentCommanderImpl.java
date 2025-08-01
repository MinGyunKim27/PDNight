package org.example.pdnight.domain.post.infra.comment;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.post.domain.comment.Comment;
import org.example.pdnight.domain.post.domain.comment.CommentCommander;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CommentCommanderImpl implements CommentCommander {

    private final CommentJpaRepository commentJpaRepository;

    @Override
    public void deleteAllByParentId(Long parentId) {
        commentJpaRepository.deleteAllByParentId(parentId);
    }

    @Override
    public void deleteAllByChildrenPostId(Long postId) {
        commentJpaRepository.deleteAllByChildrenPostId(postId);
    }

    @Override
    public void deleteAllByPostId(Long postId) {
        commentJpaRepository.deleteAllByPostId(postId);
    }

    @Override
    public Comment save(Comment comment) {
        return commentJpaRepository.save(comment);
    }

    @Override
    public Optional<Comment> findById(Long id) {
        return commentJpaRepository.findById(id);
    }

    @Override
    public void delete(Comment comment) {
        commentJpaRepository.delete(comment);
    }

}