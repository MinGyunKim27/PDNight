package org.example.pdnight.domain.user.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.pdnight.domain.auth.presentation.dto.request.SignupRequestDto;
import org.example.pdnight.domain.common.entity.Timestamped;
import org.example.pdnight.domain.common.enums.ErrorCode;
import org.example.pdnight.domain.common.enums.JobCategory;
import org.example.pdnight.domain.common.exception.BaseException;
import org.example.pdnight.domain.post.enums.Gender;
import org.example.pdnight.domain.user.domain.enums.Region;

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
    private Long id;

    @Column(nullable = false)
    private String name;

    private String nickname;

    private Set<Long> userHobbyIds = new HashSet<>();

    private Set<Long> userTechIds = new HashSet<>();

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

    //유저 삭제하면 팔로우 알아서 삭제 되도록
    @OneToMany(mappedBy = "follower", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Follow> followList = new ArrayList<>();

    //유저 삭제하면 팔로잉 알아서 삭제 되도록
    @OneToMany(mappedBy = "following", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Follow> followingList = new ArrayList<>();

    private final List<Long> couponIdList = new ArrayList<>();

    private Boolean isDeleted = false;
    private LocalDateTime deletedAt;

    // todo: id도 직접 만들어줌
    private User(Long id, String name, String nickname, Gender gender,
                 Long age, JobCategory jobCategory, Region region, Region workLocation, String comment
    ) {
        this.id = id;
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

    private User(String name, String nickname, Gender gender,
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

    // 테스트용
    private User(String name) {
        this.name = name;
        this.age = 20L;
        this.totalRate = 0L;
        this.totalReviewer = 0L;
        this.isDeleted = false;
        this.deletedAt = null;
    }

    // 유저생성 메서드
    public static User create(String name, String nickname, Gender gender,
                              Long age, JobCategory jobCategory, Region region, Region workLocation, String comment
    ) {
        return new User(name, nickname, gender, age, jobCategory, region, workLocation, comment);
    }

    // 어드민 생성 메서드
    public static User createAdmin(String name) {
        return new User(name, name, Gender.MALE,
                25L, JobCategory.BACK_END_DEVELOPER, Region.PANGYO_DONG, Region.PANGYO_DONG, null);
    }

    //테스트 유저 생성 메서드
    public static User createTestUser(Long id, String name, String email, String password) {
        return new User(name);
    }

    // todo: 이벤트용
    public static User fromUserEvent(Long userId, SignupRequestDto request) {
        return new User(userId, request.getName(), request.getNickname(), request.getGender(),
                request.getAge(), request.getJobCategory(), request.getRegion(), request.getWorkLocation(),
                request.getComment()
        );
    }

    public void setHobbyAndTech(Set<Long> userHobbyIds, Set<Long> userTechIds) {
        this.userHobbyIds = userHobbyIds;
        this.userTechIds = userTechIds;
    }

    public void softDelete() {
        isDeleted = true;
        deletedAt = LocalDateTime.now();
    }

    public void updateProfile(String name, String nickname, String gender, Long age, String jobCategory, String region,
                              String comment, Set<Long> userHobbies, Set<Long> userTechs) {
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
            this.userHobbyIds.clear();
            this.userHobbyIds.addAll(userHobbies);
        }
        if (userTechs != null) {
            this.userTechIds.clear();
            this.userTechIds.addAll(userTechs);
        }
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void addCoupon(Long id) {
        couponIdList.add(id);
    }

    public void validateIsSelfFollow(User targetUser, ErrorCode error) {
        if (this.id.equals(targetUser.getId())) {
            throw new BaseException(error);
        }
    }

    public void validateExistFollowing(User targetUser) {
        boolean isAlreadyFollowing = this.followingList.stream()
                .anyMatch(follow -> follow.getFollowing().getId().equals(targetUser.getId()));

        if (isAlreadyFollowing) {
            throw new BaseException(ErrorCode.ALREADY_FOLLOWING);
        }
    }

    public void validateIsNotFollowing(User targetUser, ErrorCode error) {
        boolean isFollowing = this.followingList.stream()
                .anyMatch(follow -> follow.getFollowing().getId().equals(targetUser.getId()));

        if (!isFollowing) {
            throw new BaseException(error);
        }
    }

    public void unfollow(User targetUser) {
        Follow toRemove = this.followingList.stream()
                .filter(follow -> follow.getFollowing().getId().equals(targetUser.getId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("팔로우하지 않은 유저입니다."));

        // 양쪽에서 연결 끊기
        this.followingList.remove(toRemove);
        targetUser.followList.remove(toRemove);
    }

}
