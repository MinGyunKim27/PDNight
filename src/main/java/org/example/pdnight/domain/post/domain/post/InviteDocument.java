package org.example.pdnight.domain.post.domain.post;

import lombok.Getter;
import org.springframework.data.elasticsearch.annotations.Document;

@Getter
@Document(indexName = "invites")
public class InviteDocument {
    private final Long inviterId;

    private final Long inviteeId;

    private final Long postId;

    private InviteDocument(Long inviterId, Long inviteeId, Long postId) {
        this.inviterId = inviterId;
        this.inviteeId = inviteeId;
        this.postId = postId;
    }

    public static InviteDocument create(Long inviterId, Long inviteeId, Long postId) {
        return new InviteDocument(inviterId, inviteeId, postId);
    }
}
