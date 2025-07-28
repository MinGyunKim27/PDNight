package org.example.pdnight.domain.invite.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.pdnight.domain.common.entity.Timestamped;
import org.example.pdnight.domain.common.enums.JoinStatus;
import org.example.pdnight.domain.post.entity.Post;
import org.example.pdnight.domain.user.entity.User;

@Getter
@Setter
@Entity
@Table(name = "invites")
@AllArgsConstructor
@NoArgsConstructor
public class Invite extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inviter_id")
    private User inviter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invitee_id")
    private User invitee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @Column(name = "status")
    private JoinStatus status = JoinStatus.PENDING;

    private Invite(User inviter,User invitee,Post post){
        this.inviter = inviter;
        this.invitee = invitee;
        this.post = post;
    }

    public static Invite create(User inviter, User invitee, Post post) {
        return new Invite( inviter,invitee, post);
    }

}
