package org.example.pdnight.domain.user.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.pdnight.domain.common.enums.ErrorCode;
import org.example.pdnight.domain.common.exception.BaseException;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_coupons")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserCoupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "coupon_id")
    private Coupon coupon;

    @Column(nullable = false)
    @Getter
    private Long userId;

    @Column(nullable = false)
    private boolean isUsed;

    public void use() {
        if (this.isUsed) {
            throw new BaseException(ErrorCode.COUPON_ALREADY_USED);
        }
        if (this.coupon.getDeadlineAt() != null && this.coupon.getDeadlineAt().isBefore(LocalDateTime.now())) {
            throw new BaseException(ErrorCode.COUPON_EXPIRED);
        }
        this.isUsed = true;
    }

    public Long getUserId() { return userId; }

    public boolean isUsed() { return isUsed; }

    public Coupon getCoupon() { return coupon; } // 필요할 때만
}
