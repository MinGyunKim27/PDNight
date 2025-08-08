package org.example.pdnight.domain.post.domain.post;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.pdnight.domain.post.enums.AgeLimit;
import org.example.pdnight.domain.post.enums.Gender;
import org.example.pdnight.domain.post.enums.PostStatus;
import org.example.pdnight.global.common.entity.Timestamped;
import org.example.pdnight.global.common.enums.JobCategory;

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

    @Setter
    @Enumerated(EnumType.STRING)
    private PostStatus status;

    @Column(nullable = false)
    private Integer maxParticipants;

    @Enumerated(EnumType.STRING)
    private Gender genderLimit = Gender.ALL;

    @Enumerated(EnumType.STRING)
    private JobCategory jobCategoryLimit = JobCategory.ALL;

    @Enumerated(EnumType.STRING)
    private AgeLimit ageLimit = AgeLimit.ALL;

    @Column(nullable = false)
    private Boolean isFirstCome = false;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostLike> postLikes = new ArrayList<>();

    // 참여자 리스트 전체를 가져와서 서버에서 구분하는 것
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostParticipant> postParticipants = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Invite> invites = new ArrayList<>();


    private Boolean isDeleted = false;
    private LocalDateTime deletedAt;

    private Post(
            Long authorId,
            String title,
            LocalDateTime timeSlot,
            String publicContent,
            Integer maxParticipants,
            Gender genderLimit,
            JobCategory jobCategoryLimit,
            AgeLimit ageLimit,
            Boolean isFirstCome
    ) {
        this.authorId = authorId;
        this.title = title;
        this.timeSlot = timeSlot;
        this.publicContent = publicContent;
        this.status = PostStatus.OPEN;
        this.maxParticipants = maxParticipants;
        if (genderLimit != null) {
            this.genderLimit = genderLimit;
        }
        if (jobCategoryLimit != null) {
            this.jobCategoryLimit = jobCategoryLimit;
        }
        if (ageLimit != null) {
            this.ageLimit = ageLimit;
        }
        this.isFirstCome = isFirstCome;
    }

    public static Post createPost(
            Long authorId,
            String title,
            LocalDateTime timeSlot,
            String publicContent,
            Integer maxParticipants,
            Gender genderLimit,
            JobCategory jobCategoryLimit,
            AgeLimit ageLimit,
            Boolean isFirstCome
    ) {
        return new Post(
                authorId,
                title,
                timeSlot,
                publicContent,
                maxParticipants,
                genderLimit,
                jobCategoryLimit,
                ageLimit,
                isFirstCome
        );
    }

    //update 메서드 null 체크 후 아닌 값만 set
    public void updatePostIfNotNull(
            String title,
            LocalDateTime timeSlot,
            String publicContent,
            Integer maxParticipants,
            Gender genderLimit,
            JobCategory jobCategoryLimit,
            AgeLimit ageLimit
    ) {
        if (title != null) this.title = title;
        if (timeSlot != null) this.timeSlot = timeSlot;
        if (publicContent != null) this.publicContent = publicContent;
        if (maxParticipants != null && maxParticipants >= 1) this.maxParticipants = maxParticipants;
        if (genderLimit != null) this.genderLimit = genderLimit;
        if (jobCategoryLimit != null) this.jobCategoryLimit = jobCategoryLimit;
        if (ageLimit != null) this.ageLimit = ageLimit;
    }

    //상태 변경 메서드
    public void softDelete() {
        isDeleted = true;
        deletedAt = LocalDateTime.now();
    }

    public void updateStatus(PostStatus status) {
        this.status = status;
    }

    public void addLike(PostLike postLike) {
        postLikes.add(postLike);
    }

    public void addParticipants(PostParticipant participant) {
        postParticipants.add(participant);
    }

    public void removeLike(PostLike postLike) {
        postLikes.remove(postLike);
    }

    public void removeParticipant(PostParticipant postParticipant) {
        postParticipants.remove(postParticipant);
    }

    public void addInvite(Invite invite) {
        invites.add(invite);
    }

    public void removeInvite(Invite findInvite) {
        invites.remove(findInvite);
    }

    public void removeAuthor() {
        this.authorId = 0L;
    }

}