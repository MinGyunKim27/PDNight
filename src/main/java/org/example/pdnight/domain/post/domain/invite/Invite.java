package org.example.pdnight.domain.post.domain.invite;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.pdnight.domain.post.enums.JoinStatus;
import org.example.pdnight.global.common.entity.Timestamped;


@Getter
@Setter
@Entity
@Table(name = "invites")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Invite extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long inviterId;

    private Long inviteeId;

    private Long postId;

    @Column(name = "status")
    private JoinStatus status = JoinStatus.PENDING;

    private Invite(Long inviterId,Long inviteeId, Long postId){
        this.inviterId = inviterId;
        this.inviteeId = inviteeId;
        this.postId = postId;
    }

    public static Invite create(Long inviterId,Long inviteeId,Long postId) {
        return new Invite( inviterId,inviteeId, postId);
    }

}
