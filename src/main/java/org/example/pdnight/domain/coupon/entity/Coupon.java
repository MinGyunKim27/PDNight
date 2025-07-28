package org.example.pdnight.domain.coupon.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.pdnight.domain.common.enums.ErrorCode;
import org.example.pdnight.domain.common.exception.BaseException;
import org.example.pdnight.domain.coupon.dto.request.UpdateCouponRequestDto;
import org.example.pdnight.domain.user.entity.User;

import java.time.LocalDateTime;


@Entity
@Table(name = "coupons")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String couponInfo;

    @Column(nullable = false)
    private boolean isUsed;

    private LocalDateTime deadlineAt;

    private Coupon(User user, String couponInfo, LocalDateTime deadlineAt) {
        this.user = user;
        this.couponInfo = couponInfo;
        this.isUsed = false;
        this.deadlineAt = deadlineAt;
    }

    public static Coupon create(User user, String couponInfo, LocalDateTime deadlineAt) {
        return new Coupon(user, couponInfo, deadlineAt);
    }

    public void updateCoupon(UpdateCouponRequestDto dto) {
        if (dto.getCouponInfo() != null && !dto.getCouponInfo().isEmpty()) {
            this.couponInfo = dto.getCouponInfo();
        }
        if (dto.getDeadlineAt() != null) {
            this.deadlineAt = dto.getDeadlineAt();
        }
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
}
