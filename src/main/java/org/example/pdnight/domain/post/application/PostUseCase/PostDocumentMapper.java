package org.example.pdnight.domain.post.application.PostUseCase;

import org.example.pdnight.domain.post.domain.post.*;

import java.util.List;

public class PostDocumentMapper {   // Entity를 Elasticsearch 전송용 Dto로 변환
    public static PostDocument from(Post post, List<String> tagNames){
        return PostDocument.createPostDocument(
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
                post.getPostLikes().stream().map(
                        postLike -> PostLikeDocument.create(postLike.getId(), postLike.getUserId())
                ).toList(),
                post.getPostParticipants().stream().map(
                        postParticipant -> PostParticipantDocument.create(postParticipant.getId(), postParticipant.getUserId(), postParticipant.getStatus(), postParticipant.getCreatedAt())
                ).toList(),
                post.getInvites().stream().map(
                        invite -> InviteDocument.create(invite.getInviterId(), invite.getInviteeId(), post.getId())
                ).toList(),
                tagNames,
                post.getIsDeleted(),
                post.getDeletedAt(),
                post.getCreatedAt()
        );
    }
}
