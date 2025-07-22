package org.example.pdnight.domain.common.exception;

import lombok.Getter;
import org.example.pdnight.domain.common.enums.ErrorCode;
import org.springframework.http.HttpStatus;

@Getter
public class BaseException extends RuntimeException {

    private final HttpStatus status;

    private final String message;

    public BaseException(HttpStatus status, String message) {
        super(message);
        this.status = status;
        this.message = message;
    }

    public BaseException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.status = errorCode.getStatus();
        this.message = errorCode.getMessage();
    }
}