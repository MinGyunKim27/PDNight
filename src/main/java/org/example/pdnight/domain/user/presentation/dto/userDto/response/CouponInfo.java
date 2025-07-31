package org.example.pdnight.domain.user.presentation.dto.userDto.response;

import lombok.Getter;
import org.example.pdnight.domain.common.enums.JobCategory;
import org.example.pdnight.domain.post.enums.Gender;

@Getter
public class CouponInfo {
    private Integer defaultDeadlineDays;

    private CouponInfo(Integer defaultDeadlineDays){
        this.defaultDeadlineDays = defaultDeadlineDays;
    }

    public static CouponInfo from(Integer defaultDeadlineDays){
        return new CouponInfo(defaultDeadlineDays);
    }
}
