package org.example.pdnight.domain.user.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.pdnight.domain.common.entity.Timestamped;
import org.example.pdnight.domain.common.enums.ErrorCode;
import org.example.pdnight.domain.common.exception.BaseException;
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

    public void use() {
        if (this.isUsed) {
            throw new BaseException(ErrorCode.COUPON_ALREADY_USED);
        }
        if (this.deadlineAt != null && this.deadlineAt.isBefore(LocalDateTime.now())) {
            throw new BaseException(ErrorCode.COUPON_EXPIRED);
        }
        this.isUsed = true;
    }

    public Long getUserId() { return user.getId(); }

    public boolean isUsed() { return isUsed; }

    public UserCoupon getCoupon() { return this; } // 필요할 때만
}
