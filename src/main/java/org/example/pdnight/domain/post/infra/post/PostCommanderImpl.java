package org.example.pdnight.domain.post.infra.post;

import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.post.domain.post.*;
import org.example.pdnight.domain.post.enums.PostStatus;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PostCommanderImpl implements PostCommander {

    private final PostJpaRepository postJpaRepository;
    private final PostESRepository postESRepository;

    @Override
    public Optional<Post> findByIdAndStatus(Long id, PostStatus status) {
        return postJpaRepository.findByIdAndStatus(id, status);
    }

    @Override
    public boolean existsByIdAndIsDeletedIsFalse(Long id) {
        return postJpaRepository.existsByIdAndIsDeletedIsFalse(id);
    }

    @Override
    public Optional<Post> findByIdAndIsDeletedIsFalse(Long id) {
        return postJpaRepository.findById(id);
    }

    @Override
    public void deletePost(Post post) {
        postJpaRepository.delete(post);
        postESRepository.delete(PostDocument.createPostDocument(
                post.getId(),
                post.getAuthorId(),
                post.getTitle(),
                post.getTimeSlot(),
                post.getPublicContent(),
                post.getStatus(),
                post.getMaxParticipants(),
                post.getGenderLimit(),
                post.getJobCategoryLimit(),
                post.getAgeLimit(),
                post.getIsFirstCome(),
                post.getPostLikes().stream()
                        .map(postLike-> PostLikeDocument.create(postLike.getPost().getId(), postLike.getUserId()))
                        .toList(),
                post.getPostParticipants().stream()
                        .map(postParticipant-> PostParticipantDocument.create(postParticipant.getPost().getId(), postParticipant.getUserId(), postParticipant.getStatus(), postParticipant.getCreatedAt()))
                        .toList(),
                post.getInvites().stream()
                        .map(invite-> InviteDocument.create(invite.getInviterId(), invite.getInviteeId(), invite.getPost().getId()))
                        .toList(),
                post.getPostTagList().stream()
                        .map(postTag -> PostTagDocument.create(postTag.getId(), postTag.getTagId()))
                        .toList(),
                post.getIsDeleted(),
                post.getDeletedAt(),
                post.getCreatedAt()));
    }

    @Override
    public Post save(Post post) {
        Post savePost = postJpaRepository.save(post);
        saveES(savePost);

        return savePost;
    }

    @Override
    public List<Post> findAllByAuthorId(Long authorId) {
        return postJpaRepository.findAllByAuthorId(authorId);
    }

    @Override
    public void saveES(Post foundPost) {
        postESRepository.save(PostDocument.createPostDocument(
                foundPost.getId(),
                foundPost.getAuthorId(),
                foundPost.getTitle(),
                foundPost.getTimeSlot(),
                foundPost.getPublicContent(),
                foundPost.getStatus(),
                foundPost.getMaxParticipants(),
                foundPost.getGenderLimit(),
                foundPost.getJobCategoryLimit(),
                foundPost.getAgeLimit(),
                foundPost.getIsFirstCome(),
                foundPost.getPostLikes().stream()
                        .map(postLike-> PostLikeDocument.create(postLike.getPost().getId(), postLike.getUserId()))
                        .toList(),
                foundPost.getPostParticipants().stream()
                        .map(postParticipant-> PostParticipantDocument.create(postParticipant.getPost().getId(), postParticipant.getUserId(), postParticipant.getStatus(), postParticipant.getCreatedAt()))
                        .toList(),
                foundPost.getInvites().stream()
                        .map(invite-> InviteDocument.create(invite.getInviterId(), invite.getInviteeId(), invite.getPost().getId()))
                        .toList(),
                foundPost.getPostTagList().stream()
                        .map(postTag -> PostTagDocument.create(postTag.getId(), postTag.getTagId()))
                        .toList(),
                foundPost.getIsDeleted(),
                foundPost.getDeletedAt(),
                foundPost.getCreatedAt()
        ));
    }
}
