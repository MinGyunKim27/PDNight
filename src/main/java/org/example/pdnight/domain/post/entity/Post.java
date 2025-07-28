package org.example.pdnight.domain.post.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.pdnight.domain.common.entity.Timestamped;
import org.example.pdnight.domain.common.enums.JobCategory;
import org.example.pdnight.domain.hobby.entity.PostHobby;
import org.example.pdnight.domain.invite.entity.Invite;
import org.example.pdnight.domain.post.enums.AgeLimit;
import org.example.pdnight.domain.post.enums.Gender;
import org.example.pdnight.domain.post.enums.PostStatus;
import org.example.pdnight.domain.postLike.entity.PostLike;
import org.example.pdnight.domain.review.entity.Review;
import org.example.pdnight.domain.techStack.entity.PostTech;
import org.example.pdnight.domain.user.entity.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "posts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private User author;

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

    // 게시물 삭제 되면 알아서 invite 삭제 되도록
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Invite> invites = new ArrayList<>();

    @Column(nullable = false)
    private Boolean isFirstCome = false;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PostHobby> postHobbies = new HashSet<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PostTech> postTechs = new HashSet<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostLike> postLikes = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Review> reviews = new ArrayList<>();


    private Post(
            User author, String title, LocalDateTime timeSlot, String publicContent, String privateContent,
            Integer maxParticipants, Gender genderLimit, JobCategory jobCategoryLimit, AgeLimit ageLimit
    ) {
        this.author = author;
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

    private Post(User author, String title, LocalDateTime timeSlot, String publicContent, String privateContent,
                 Integer maxParticipants, Gender genderLimit, JobCategory jobCategoryLimit, AgeLimit ageLimit, Boolean isFirstCome, PostStatus status) {
        this.author = author;
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
            User author, String title, LocalDateTime timeSlot, String publicContent, String privateContent,
            Integer maxParticipants, Gender genderLimit, JobCategory jobCategoryLimit, AgeLimit ageLimit
    ) {
        return new Post(author, title, timeSlot, publicContent, privateContent, maxParticipants, genderLimit, jobCategoryLimit, ageLimit);
    }

    public static Post createPost(User author, String title, LocalDateTime timeSlot, String publicContent, String privateContent,
                                  Integer maxParticipants, Gender genderLimit, JobCategory jobCategoryLimit, AgeLimit ageLimit, Boolean isFirstCome, PostStatus status) {
        return new Post(author, title, timeSlot, publicContent, privateContent, maxParticipants, genderLimit, jobCategoryLimit, ageLimit, isFirstCome, status);
    }

    public void setHobbyAndTech(Set<PostHobby> postHobbies, Set<PostTech> postTechs) {
        this.postHobbies = postHobbies;
        this.postTechs = postTechs;
    }

    //update 메서드 null 체크 후 아닌 값만 set
    public void updatePostIfNotNull(
            String title, LocalDateTime timeSlot, String publicContent, String privateContent,
            Integer maxParticipants, Gender genderLimit, JobCategory jobCategoryLimit, AgeLimit ageLimit,
            Set<PostHobby> postHobbies, Set<PostTech> postTechs
    ) {
        if (title != null) this.title = title;
        if (timeSlot != null) this.timeSlot = timeSlot;
        if (publicContent != null) this.publicContent = publicContent;
        if (privateContent != null) this.privateContent = privateContent;
        if (maxParticipants != null && maxParticipants >= 1) this.maxParticipants = maxParticipants;
        if (genderLimit != null) this.genderLimit = genderLimit;
        if (jobCategoryLimit != null) this.jobCategoryLimit = jobCategoryLimit;
        if (ageLimit != null) this.ageLimit = ageLimit;
        if (postHobbies != null) {
            this.postHobbies.clear();
            this.postHobbies.addAll(postHobbies);
        }
        if (postTechs != null) {
            this.postTechs.clear();
            this.postTechs.addAll(postTechs);
        }
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

    public void addReview(Review review) {
        this.reviews.add(review);
    }

    public void unlinkReviews() {
        for (Review review : reviews) {
            review.removePost();
        }
    }
}
