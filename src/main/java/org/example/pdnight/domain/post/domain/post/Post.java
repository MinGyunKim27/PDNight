package org.example.pdnight.domain.post.domain.post;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.pdnight.domain.post.enums.AgeLimit;
import org.example.pdnight.domain.post.enums.Gender;
import org.example.pdnight.domain.post.enums.JobCategory;
import org.example.pdnight.domain.post.enums.PostStatus;
import org.example.pdnight.domain1.common.entity.Timestamped;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "posts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long authorId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private LocalDateTime timeSlot;

    private String publicContent;
    private String privateContent;

    @Setter
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

    private List<Long> invites = new ArrayList<>();

    @Column(nullable = false)
    private Boolean isFirstCome = false;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostLike> postLikes = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostParticipant> postParticipants = new ArrayList<>();

    private Post(
            Long authorId, String title, LocalDateTime timeSlot, String publicContent, String privateContent,
            Integer maxParticipants, Gender genderLimit, JobCategory jobCategoryLimit, AgeLimit ageLimit
    ) {
        this.authorId = authorId;
        this.title = title;
        this.timeSlot = timeSlot;
        this.publicContent = publicContent;
        this.privateContent = privateContent;
        this.status = PostStatus.OPEN;
        this.maxParticipants = maxParticipants;
        this.genderLimit = genderLimit;
        this.jobCategoryLimit = jobCategoryLimit;
        this.ageLimit = ageLimit;
    }

    private Post(Long authorId, String title, LocalDateTime timeSlot, String publicContent, String privateContent,
                 Integer maxParticipants, Gender genderLimit, JobCategory jobCategoryLimit, AgeLimit ageLimit, Boolean isFirstCome, PostStatus status) {
        this.authorId = authorId;
        this.title = title;
        this.timeSlot = timeSlot;
        this.publicContent = publicContent;
        this.privateContent = privateContent;
        this.status = PostStatus.OPEN;
        this.maxParticipants = maxParticipants;
        this.genderLimit = genderLimit;
        this.jobCategoryLimit = jobCategoryLimit;
        this.ageLimit = ageLimit;
        this.isFirstCome = isFirstCome;
        this.status = status;
    }

    public static Post createPost(
            Long authorId, String title, LocalDateTime timeSlot, String publicContent, String privateContent,
            Integer maxParticipants, Gender genderLimit, JobCategory jobCategoryLimit, AgeLimit ageLimit
    ) {
        return new Post(authorId, title, timeSlot, publicContent, privateContent, maxParticipants, genderLimit, jobCategoryLimit, ageLimit);
    }

    public static Post createPost(
            Long authorId, String title, LocalDateTime timeSlot, String publicContent, String privateContent,
            Integer maxParticipants, Gender genderLimit, JobCategory jobCategoryLimit, AgeLimit ageLimit,
            Boolean isFirstCome, PostStatus status
    )
    {
        return new Post(authorId, title, timeSlot, publicContent, privateContent, maxParticipants, genderLimit, jobCategoryLimit, ageLimit, isFirstCome, status);
    }

    //update 메서드 null 체크 후 아닌 값만 set
    public void updatePostIfNotNull(
            String title, LocalDateTime timeSlot, String publicContent, String privateContent,
            Integer maxParticipants, Gender genderLimit, JobCategory jobCategoryLimit, AgeLimit ageLimit
    ) {
        if (title != null) this.title = title;
        if (timeSlot != null) this.timeSlot = timeSlot;
        if (publicContent != null) this.publicContent = publicContent;
        if (privateContent != null) this.privateContent = privateContent;
        if (maxParticipants != null && maxParticipants >= 1) this.maxParticipants = maxParticipants;
        if (genderLimit != null) this.genderLimit = genderLimit;
        if (jobCategoryLimit != null) this.jobCategoryLimit = jobCategoryLimit;
        if (ageLimit != null) this.ageLimit = ageLimit;

    }

    //상태 변경 메서드
    public void updateStatus(PostStatus status) {
        this.status = status;
    }

    public void addLike(PostLike postLike) {
        this.postLikes.add(postLike);
    }

    public void removeLike(PostLike postLike) {
        this.postLikes.remove(postLike);
    }

}