package org.example.pdnight.domain.post.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.pdnight.domain.common.entity.Timestamped;
import org.example.pdnight.domain.post.enums.AgeLimit;
import org.example.pdnight.domain.post.enums.Gender;
import org.example.pdnight.domain.common.enums.JobCategory;
import org.example.pdnight.domain.post.enums.PostStatus;
import org.example.pdnight.domain.user.entity.User;
import java.time.LocalTime;

@Entity
@Table(name = "posts")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Post extends Timestamped {
    @Id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private User author;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private LocalTime timeSlot;

    private String publicContent;
    private String privateContent;

    @Enumerated(EnumType.STRING)
    private PostStatus status;

    @Column(nullable = false)
    private Integer maxParticipants;

    @Enumerated(EnumType.STRING)
    private Gender genderLimit;

    @Enumerated(EnumType.STRING)
    private JobCategory jobCategoryLimit;

    @Enumerated(EnumType.STRING)
    private AgeLimit ageLimit;
}
