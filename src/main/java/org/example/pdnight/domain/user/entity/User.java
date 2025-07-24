package org.example.pdnight.domain.user.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.pdnight.domain.auth.dto.request.SignupRequestDto;
import org.example.pdnight.domain.common.entity.Timestamped;
import org.example.pdnight.domain.common.enums.JobCategory;
import org.example.pdnight.domain.common.enums.UserRole;
import org.example.pdnight.domain.hobby.entity.UserHobby;
import org.example.pdnight.domain.post.enums.Gender;
import org.example.pdnight.domain.techStack.entity.UserTech;
import org.example.pdnight.domain.user.dto.request.UserUpdateRequest;
import org.example.pdnight.domain.user.enums.Region;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class User extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Column(nullable = false)
    private String name;

    private String nickname;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserHobby> userHobbyList = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserTech> userTechList = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private Long age;

    @Enumerated(EnumType.STRING)
    private JobCategory jobCategory;

    @Enumerated(EnumType.STRING)
    private Region region;

    @Enumerated(EnumType.STRING)
    @Column(name = "work_location")
    private Region workLocation = Region.PANGYO_DONG;

    private String comment;

    private Long totalRate;
    private Long totalReviewer;

    private Boolean isDeleted = false;
    private LocalDateTime deletedAt;

    public User(SignupRequestDto request, String encodePassword) {
        this.email = request.getEmail();
        this.password = encodePassword;
        this.role = UserRole.USER;
        this.name = request.getName();
        this.nickname = request.getNickname();
        this.gender = request.getGender();
        this.age = request.getAge();
        this.jobCategory = request.getJobCategory();
        this.region = request.getRegion();
        this.workLocation = request.getWorkLocation();
        this.comment = request.getComment();
        this.totalRate = 0L;
        this.totalReviewer = 0L;
    }

    public void setHobbyAndTech(List<UserHobby> userHobbyList, List<UserTech> userTechList) {
        this.userHobbyList = userHobbyList;
        this.userTechList = userTechList;
    }

    public void softDelete() {
        isDeleted = true;
        deletedAt = LocalDateTime.now();
    }

    public void updateProfile(UserUpdateRequest request, List<UserHobby> userHobbyList, List<UserTech> userTechList) {
        if (request.getName() != null) {
            this.name = request.getName();
        }
        if (request.getNickname() != null) {
            this.nickname = request.getNickname();
        }
        if (request.getGender() != null) {
            this.gender = Gender.valueOf(request.getGender());
        }
        if (request.getAge() != null) {
            this.age = request.getAge();
        }
        if (request.getJobCategory() != null) {
            this.jobCategory = JobCategory.valueOf(request.getJobCategory());
        }
        if (request.getRegion() != null) {
            this.region = Region.valueOf(request.getRegion());
        }
        if (request.getComment() != null) {
            this.comment = request.getComment();
        }
        if (userHobbyList != null) {
            this.userHobbyList.clear();
            this.userHobbyList.addAll(userHobbyList);
        }
        if (userTechList != null) {
            this.userTechList.clear();
            this.userTechList.addAll(userTechList);
        }
    }

    public void changePassword(String encodedPassword) {
        this.password = encodedPassword;
    }

}
