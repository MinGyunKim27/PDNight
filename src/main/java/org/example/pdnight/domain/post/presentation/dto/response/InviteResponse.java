package org.example.pdnight.domain.post.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import org.example.pdnight.domain.post.domain.post.Invite;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InviteResponse {
    private Long id;
    private Long inviteeId;
    private Long postId;

    private InviteResponse(Invite invite) {
        this.id = invite.getId();
        this.inviteeId = invite.getInviteeId();
        this.postId = invite.getPost().getId();
    }

    @QueryProjection
    public InviteResponse(
            Long id, Long inviteeId, Long postId) {
        this.id = id;
        this.inviteeId = inviteeId;
        this.postId = postId;
    }

    public InviteResponse(Long inviteeId, Long postId) {
        this.inviteeId = inviteeId;
        this.postId = postId;
    }

    public static InviteResponse from(Invite invite) {
        return new InviteResponse(invite);
    }

    public static InviteResponse from(Long inviteeId, Long postId) {
        return new InviteResponse(inviteeId, postId);
    }

}
