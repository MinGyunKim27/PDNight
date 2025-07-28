package org.example.pdnight.domain.user.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.pdnight.domain.auth.dto.request.SignupRequestDto;
import org.example.pdnight.domain.common.entity.Timestamped;
import org.example.pdnight.domain.common.enums.JobCategory;
import org.example.pdnight.domain.common.enums.UserRole;
import org.example.pdnight.domain.coupon.entity.Coupon;
import org.example.pdnight.domain.follow.entity.Follow;
import org.example.pdnight.domain.hobby.entity.UserHobby;
import org.example.pdnight.domain.invite.entity.Invite;
import org.example.pdnight.domain.post.enums.Gender;
import org.example.pdnight.domain.techStack.entity.UserTech;
import org.example.pdnight.domain.user.dto.request.UserUpdateRequest;
import org.example.pdnight.domain.user.enums.Region;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor
@AllArgsConstructor
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

    public User(Long id, String name) {
        this.id = id;
        this.name = name;
        this.role = UserRole.USER;
        this.totalRate = 0L;
        this.totalReviewer = 0L;
        this.isDeleted = false;
        this.deletedAt = null;
    }

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

    public User(Long id, String name, String hashedOldPassword) {
        this.id = id;
        this.name = name;
        this.password = hashedOldPassword;
    }

    //테스트용
    public User(Long id) {
        this.id = id;
        this.role = UserRole.USER;
    }


    public User(Long userId, String name, Long totalRate, Long totalReviewer) {
        this.id = userId;
        this.name = name;
        this.totalRate = totalRate;
        this.totalReviewer = totalReviewer;
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

    //어드민 생성 메서드
    public static User createAdmin(String email, String name, String password) {
        User admin = new User(email, name, password);
        admin.role = UserRole.ADMIN;
        return admin;
    }

    //테스트 유저 생성 메서드
    public static User createTestUser(Long id, String name,String email,String password) {
        User test = new User(email,name, password);
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

    public void updateProfile(UserUpdateRequest request, Set<UserHobby> userHobbies, Set<UserTech> userTechs) {
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
        if (region != null) {
            this.region = Region.valueOf(region);
        }
        if (request.getComment() != null) {
            this.comment = request.getComment();
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
