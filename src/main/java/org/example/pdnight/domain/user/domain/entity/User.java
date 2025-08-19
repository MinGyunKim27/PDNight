package org.example.pdnight.domain.user.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.pdnight.domain.post.enums.Gender;
import org.example.pdnight.domain.user.domain.enums.Region;
import org.example.pdnight.global.common.entity.Timestamped;
import org.example.pdnight.global.common.enums.ErrorCode;
import org.example.pdnight.global.common.enums.JobCategory;
import org.example.pdnight.global.common.exception.BaseException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.CascadeType.ALL;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends Timestamped {

    @Setter
    @Id
    private Long id;

    @Column(nullable = false)
    private String name;

    private String nickname;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private Long age;

    @Enumerated(EnumType.STRING)
    private JobCategory jobCategory;

    @OneToMany(mappedBy = "user", cascade = ALL, orphanRemoval = true)
    private List<UserHobby> userHobbies = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = ALL, orphanRemoval = true)
    private List<UserTech> userTeckStacks = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private Region region;

    @Enumerated(EnumType.STRING)
    @Column(name = "work_location")
    private Region workLocation = Region.PANGYO_DONG;

    private String comment;

    private Long totalRate = 0L;
    private Long totalReviewer;

    // 쿠폰
    @OneToMany(mappedBy = "user", cascade = ALL, orphanRemoval = true)
    private List<UserCoupon> userCoupons = new ArrayList<>();

    /**
     * 내가 팔로우 하는 사람 목록<br>
     * 언팔로우 시 검증 하게 됨
     */
    @OneToMany(mappedBy = "follower", cascade = ALL, orphanRemoval = true)
    private List<Follow> followedOther = new ArrayList<>();

    /**
     * 나를 팔로우 하는 사람 목록
     */
    @OneToMany(mappedBy = "following", cascade = ALL, orphanRemoval = true)
    private List<Follow> followingMe = new ArrayList<>();

    private Boolean isDeleted = false;
    private LocalDateTime deletedAt;

    // ================================== 생성자 ==================================
    private User(
            String name, String nickname, Gender gender,
            Long age, JobCategory jobCategory, Region region, Region workLocation, String comment
    ) {
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

    private User(
            Long authId,
            String name,
            String nickname,
            Gender gender,
            Long age,
            JobCategory jobCategory
    ) {
        this.id = authId;
        this.name = name;
        this.nickname = nickname;
        this.gender = gender;
        this.age = age;
        this.jobCategory = jobCategory;
    }

    // ================================== static 생성 메서드 ==================================

    public static User fromAuthSignUpEvent(
            Long authId,
            String name,
            String nickname,
            Gender gender,
            Long age,
            JobCategory jobCategory) {
        return new User(
                authId,
                name,
                nickname,
                gender,
                age,
                jobCategory
        );
    }

    // ================================== add ==================================
    public void addFollow(User targetUser, Follow follow) {
        this.followedOther.add(follow);      // 내가 팔로잉하는 유저 목록에 추가
        targetUser.followingMe.add(follow);   // 상대 유저가 나를 팔로우하는 목록에 추가
    }

    public void addCoupon(UserCoupon userCoupon) {
        this.userCoupons.add(userCoupon);
    }

    // ================================== update ==================================
    public void updateProfile(
            String name, String nickname, String gender, Long age,
            String jobCategory, String region, String comment,
            List<Long> hobbies, List<Long> techStacks
    ) {
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
        if (hobbies != null) {
            setHobbies(hobbies);
        }
        if (techStacks != null) {
            setTechs(techStacks);
        }
    }

    public void setHobbies(List<Long> hobbies) {
        userHobbies.clear();
        for (Long hobbyId : hobbies) {
            userHobbies.add(UserHobby.create(this, hobbyId));
        }
    }

    public void setTechs(List<Long> techs) {
        userTeckStacks.clear();
        for (Long techStackId : techs) {
            userTeckStacks.add(UserTech.create(this, techStackId));
        }
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void updateEvaluation(BigDecimal rate) {
        this.totalRate = BigDecimal.valueOf(this.totalRate)
                .add(rate)
                .longValue();
        this.totalReviewer++;
    }

    // ================================== validate ==================================
    public void validateIsSelfFollow(User targetUser, ErrorCode error) {
        if (this.id.equals(targetUser.getId())) {
            throw new BaseException(error);
        }
    }

    public void validateExistFollowing(User targetUser) {
        boolean isAlreadyFollowing = this.followedOther.stream()
                .anyMatch(follow -> follow.getFollowing().getId().equals(targetUser.getId()));

        if (isAlreadyFollowing) {
            throw new BaseException(ErrorCode.ALREADY_FOLLOWING);
        }
    }

    public void validateIsNotFollowing(User targetUser, ErrorCode error) {
        boolean isFollowing = this.followedOther.stream()
                .anyMatch(follow -> follow.getFollowing().getId().equals(targetUser.getId()));

        if (!isFollowing) {
            throw new BaseException(error);
        }
    }

    // ================================== remove ==================================
    public void softDelete() {
        isDeleted = true;
        deletedAt = LocalDateTime.now();
    }

    public void unfollow(User targetUser) {
        Follow toRemove = this.followedOther.stream()
                .filter(follow -> follow.getFollowing().getId().equals(targetUser.getId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("팔로우하지 않은 유저입니다."));

        // 양쪽에서 연결 끊기
        this.followedOther.remove(toRemove);
        targetUser.followingMe.remove(toRemove);
    }

    // ----------------  테스트용 ------------------
    private User(Long id, String name) {
        this.id = id;
        this.name = name;
        this.age = 20L;
        this.totalRate = 0L;
        this.totalReviewer = 0L;
        this.isDeleted = false;
        this.deletedAt = null;
    }

    //테스트 유저 생성 메서드
    public static User createTestUser(Long id, String name, String email, String password) {
        return new User(id, name);
    }
}
