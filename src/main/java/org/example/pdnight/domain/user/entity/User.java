package org.example.pdnight.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.pdnight.domain.common.entity.Timestamped;
import org.example.pdnight.domain.common.enums.JobCategory;
import org.example.pdnight.domain.common.enums.UserRole;
import org.example.pdnight.domain.coupon.entity.Coupon;
import org.example.pdnight.domain.follow.entity.Follow;
import org.example.pdnight.domain.hobby.entity.UserHobby;
import org.example.pdnight.domain.invite.entity.Invite;
import org.example.pdnight.domain.post.enums.Gender;
import org.example.pdnight.domain.techStack.entity.UserTech;
import org.example.pdnight.domain.user.enums.Region;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter
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
    private Set<UserHobby> userHobbies = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserTech> userTechs = new HashSet<>();

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

    //유저 삭제 하면 초대 알아서 삭제 되도록
    @OneToMany(mappedBy = "inviter", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Invite> sentInvites = new ArrayList<>();

    //유저 삭제 하면 초대 알아서 삭제 되도록
    @OneToMany(mappedBy = "invitee", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Invite> receivedInvites = new ArrayList<>();

    //유저 삭제하면 팔로우 알아서 삭제 되도록
    @OneToMany(mappedBy = "follower", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Follow> followList = new ArrayList<>();

    //유저 삭제하면 팔로잉 알아서 삭제 되도록
    @OneToMany(mappedBy = "following", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Follow> followingList = new ArrayList<>();

    private Boolean isDeleted = false;
    private LocalDateTime deletedAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Coupon> coupons = new ArrayList<>();

    private User(String email, String encodePassword, UserRole role, String name, String nickname, Gender gender,
                Long age, JobCategory jobCategory, Region region, Region workLocation, String comment
                ) {
        this.email = email;
        this.password = encodePassword;
        this.role = role == null? UserRole.USER : role;
        this.name = name;
        this.nickname = nickname;
        this.gender = gender;
        this.age = age;
        this.jobCategory = jobCategory;
        this.region = region;
        this.workLocation = workLocation;
        this.comment = comment;
        this.totalRate = 0L;
        this.totalReviewer = 0L;
    }

    // 유저생성 메서드
    public static User create(String email, String encodePassword, UserRole role, String name, String nickname, Gender gender,
                              Long age, JobCategory jobCategory, Region region, Region workLocation, String comment
    ) {
        return new User(email, encodePassword, role, name, nickname, gender,
                age, jobCategory, region, workLocation, comment);
    }

    // 어드민 생성 메서드
    public static User createAdmin(String email, String name, String password) {
        return new User(email, password, UserRole.ADMIN, name, name, Gender.MALE,
                25L, JobCategory.BACK_END_DEVELOPER, Region.PANGYO_DONG, Region.PANGYO_DONG, null);
    }

    // 테스트용
    private User(String email, String name, String password) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.role = UserRole.USER;
        this.age = 20L;
        this.totalRate = 0L;
        this.totalReviewer = 0L;
        this.isDeleted = false;
        this.deletedAt = null;
    }

    //테스트 유저 생성 메서드
    public static User createTestUser(Long id, String name, String email, String password) {
        User test = new User(email, name, password);
        test.role = UserRole.ADMIN;
        return test;
    }


    public void setHobbyAndTech(Set<UserHobby> userHobbies, Set<UserTech> userTechs) {
        this.userHobbies = userHobbies;
        this.userTechs = userTechs;
    }

    public void softDelete() {
        isDeleted = true;
        deletedAt = LocalDateTime.now();
    }

    public void updateProfile(String name, String nickname, String gender, Long age, String jobCategory, String region,
                String comment, Set<UserHobby> userHobbies, Set<UserTech> userTechs) {
        if (name != null) {
            this.name = name;
        }
        if (nickname != null) {
            this.nickname = nickname;
        }
        if (gender != null) {
            this.gender = Gender.valueOf(gender);
        }
        if (age != null) {
            this.age = age;
        }
        if (jobCategory != null) {
            this.jobCategory = JobCategory.valueOf(jobCategory);
        }
        if (region != null) {
            this.region = Region.valueOf(region);
        }
        if (comment != null) {
            this.comment = comment;
        }
        if (userHobbies != null) {
            this.userHobbies.clear();
            this.userHobbies.addAll(userHobbies);
        }
        if (userTechs != null) {
            this.userTechs.clear();
            this.userTechs.addAll(userTechs);
        }
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void changePassword(String encodedPassword) {
        this.password = encodedPassword;
    }

    public void addCoupon(Coupon coupon) {
        this.coupons.add(coupon);
    }

}
