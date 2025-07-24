package org.example.pdnight.domain.common.enums;

import org.example.pdnight.domain.common.exception.BaseException;
import org.springframework.http.HttpStatus;

import java.util.Arrays;

public enum JoinStatus {
    PENDING,
    ACCEPTED,
    REJECTED;

    public static JoinStatus of(String status) {
        if (status == null) {
            throw new BaseException(HttpStatus.BAD_REQUEST, "JoinStatus은 null일 수 없습니다.");
        }
        return Arrays.stream(JoinStatus.values())
                .filter(r -> r.name().equalsIgnoreCase(status))
                .findFirst()
                .orElseThrow(() -> new BaseException(HttpStatus.BAD_REQUEST, "유효하지 않은 JoinStatus"));
    }
}
