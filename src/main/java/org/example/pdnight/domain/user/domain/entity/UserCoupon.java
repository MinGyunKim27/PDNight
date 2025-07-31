package org.example.pdnight.domain.user.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.pdnight.global.common.entity.Timestamped;
import org.example.pdnight.global.common.enums.ErrorCode;
import org.example.pdnight.global.common.exception.BaseException;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_coupons")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserCoupon extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long couponId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private boolean isUsed;

    private LocalDateTime deadlineAt;

    private UserCoupon(User user, Long couponId, LocalDateTime deadlineAt) {
        this.user = user;
        this.couponId = couponId;
        this.deadlineAt = deadlineAt;
    }

    public static UserCoupon create(User user, Long couponId, Integer defaultDeadlineDays) {
        LocalDateTime dateTime = LocalDateTime.now().plusDays(defaultDeadlineDays);
        return new UserCoupon(user, couponId, dateTime);
    }

    public void use() {
        if (this.isUsed) {
            throw new BaseException(ErrorCode.COUPON_ALREADY_USED);
        }
        if (this.deadlineAt != null && this.deadlineAt.isBefore(LocalDateTime.now())) {
            throw new BaseException(ErrorCode.COUPON_EXPIRED);
        }
        this.isUsed = true;
    }

    public Long getUserId() {
        return user.getId();
    }

    public boolean isUsed() {
        return isUsed;
    }

    public UserCoupon getCoupon() {
        return this;
    } // 필요할 때만
}
