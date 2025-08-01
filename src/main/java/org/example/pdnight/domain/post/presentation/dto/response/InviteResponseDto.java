package org.example.pdnight.domain.post.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import org.example.pdnight.domain.post.domain.post.Invite;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InviteResponseDto {
    private Long id;
    private Long inviteeId;
    private Long postId;

    private InviteResponseDto(Invite invite) {
        this.id = invite.getId();
        this.inviteeId = invite.getInviteeId();
        this.postId = invite.getPost().getId();
    }

    @QueryProjection
    public InviteResponseDto(
            Long id, Long inviteeId, Long postId) {
        this.id = id;
        this.inviteeId = inviteeId;
        this.postId = postId;
    }

    public static InviteResponseDto from(Invite invite) {
        return new InviteResponseDto(invite);
    }
}
