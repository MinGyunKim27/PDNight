package org.example.pdnight.domain.invite.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import org.example.pdnight.domain.invite.entity.Invite;

@Getter
public class InviteResponseDto {
    private Long id;
    private Long inviteeId;
    private String inviteeNickName;
    private Long postId;
    private String postTitle;

    public InviteResponseDto(Invite invite) {
        this.id = invite.getId();
        this.inviteeId = invite.getInvitee().getId();
        this.inviteeNickName = invite.getInvitee().getNickname();
        this.postId = invite.getPost().getId();
        this.postTitle = invite.getPost().getTitle();
    }

    @QueryProjection
    public InviteResponseDto(
            Long id, Long inviteeId, String inviteeNickName, Long postId, String postTitle) {
        this.id = id;
        this.inviteeId = inviteeId;
        this.inviteeNickName = inviteeNickName;
        this.postId = postId;
        this.postTitle = postTitle;
    }

    public static InviteResponseDto from(Invite invite) {
        return new InviteResponseDto(invite);
    }
}
