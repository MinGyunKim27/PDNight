package org.example.pdnight.domain.coupon.entity;

import jakarta.persistence.*;
import lombok.*;
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

    public Coupon(User user, String couponInfo, LocalDateTime deadlineAt) {
        this.user = user;
        this.couponInfo = couponInfo;
        this.isUsed = false;
        this.deadlineAt = deadlineAt;
    }

    public void updateCoupon(UpdateCouponRequestDto dto) {
        if (dto.getCouponInfo() != null && !dto.getCouponInfo().isEmpty()) {
            this.couponInfo = dto.getCouponInfo();
        }
        if (dto.getDeadlineAt() != null) {
            this.deadlineAt = dto.getDeadlineAt();
        }
    }
}
