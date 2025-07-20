package org.example.pdnight.domain.post.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.pdnight.domain.common.entity.Timestamped;
import org.example.pdnight.domain.post.enums.AgeLimit;
import org.example.pdnight.domain.post.enums.Gender;
import org.example.pdnight.domain.common.enums.JobCategory;
import org.example.pdnight.domain.post.enums.PostStatus;
import org.example.pdnight.domain.user.entity.User;

import java.time.LocalDateTime;
import java.time.LocalTime;

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

    private Post(User author, String title, LocalDateTime timeSlot, String publicContent, String privateContent,
        Integer maxParticipants, Gender genderLimit, JobCategory jobCategoryLimit, AgeLimit ageLimit)
    {
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

    public static Post createPost(User author, String title, LocalDateTime timeSlot, String publicContent, String privateContent,
        Integer maxParticipants, Gender genderLimit, JobCategory jobCategoryLimit, AgeLimit ageLimit)
    {
        return new Post(author, title, timeSlot, publicContent, privateContent, maxParticipants, genderLimit, jobCategoryLimit, ageLimit);
    }

    //update 메서드 null 체크 후 아닌 값만 set
    public void updatePostIfNotNull(String title, LocalDateTime timeSlot, String publicContent, String privateContent,
        Integer maxParticipants, Gender genderLimit, JobCategory jobCategoryLimit, AgeLimit ageLimit)
    {
        if(title != null) this.title = title;
        if(timeSlot != null) this.timeSlot = timeSlot;
        if(publicContent != null) this.publicContent = publicContent;
        if(privateContent != null) this.privateContent = privateContent;
        if(maxParticipants != null && maxParticipants >= 1) this.maxParticipants = maxParticipants;
        if(genderLimit != null) this.genderLimit = genderLimit;
        if(jobCategoryLimit != null) this.jobCategoryLimit = jobCategoryLimit;
        if(ageLimit != null) this.ageLimit = ageLimit;
    }

}
