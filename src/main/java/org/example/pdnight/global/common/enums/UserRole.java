package org.example.pdnight.global.common.enums;

import org.example.pdnight.global.common.exception.BaseException;
import org.springframework.http.HttpStatus;

import java.util.Arrays;

public enum UserRole {
    ADMIN, USER;

    public static UserRole of(String role) {
        if (role == null) {
            throw new BaseException(HttpStatus.BAD_REQUEST,"UserRole은 null일 수 없습니다.");
        }
        return Arrays.stream(UserRole.values())
                .filter(r -> r.name().equalsIgnoreCase(role))
                .findFirst()
                .orElseThrow(() -> new BaseException(HttpStatus.BAD_REQUEST,"유효하지 않은 UerRole"));
    }
}
