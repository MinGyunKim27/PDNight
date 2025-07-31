package org.example.pdnight.domain.user.infra.couponInfra;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.pdnight.domain.user.domain.couponDomain.CouponReader;
import org.example.pdnight.domain.user.domain.entity.Coupon;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import static org.example.pdnight.domain.user.domain.entity.QCoupon.coupon;

@Repository
@RequiredArgsConstructor
public class CouponReaderImpl implements CouponReader {

    private final JPAQueryFactory queryFactory;

    public Optional<Coupon> findById(Long couponId) {
        return queryFactory.selectFrom(coupon)
                .where(coupon.id.eq(couponId))
                .stream()
                .findFirst();
    }
}
