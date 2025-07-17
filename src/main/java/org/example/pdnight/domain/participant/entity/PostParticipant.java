package org.example.pdnight.domain.participant.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.pdnight.domain.common.entity.Timestamped;
import org.example.pdnight.domain.participant.enums.JoinStatus;
import org.example.pdnight.domain.post.entity.Post;
import org.example.pdnight.domain.user.entity.User;

@Entity
@Table(name = "posts_participants")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostParticipant extends Timestamped{
    @Id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private JoinStatus status;
}
