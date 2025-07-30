package org.example.pdnight.domain.post.presentation.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import org.example.pdnight.domain.post.domain.invite.Invite;

@Getter
public class InviteResponseDto {
    private Long id;
    private Long inviteeId;
    private Long postId;
    private String postTitle;

    private InviteResponseDto(Invite invite) {
        this.id = invite.getId();
        this.inviteeId = invite.getInviteeId();
        this.postId = invite.getPost().getId();
        this.postTitle = invite.getPost().getTitle();
    }

    @QueryProjection
    public InviteResponseDto(
            Long id, Long inviteeId, Long postId, String postTitle) {
        this.id = id;
        this.inviteeId = inviteeId;
        this.postId = postId;
        this.postTitle = postTitle;
    }

    public static InviteResponseDto from(Invite invite) {
        return new InviteResponseDto(invite);
    }
}
