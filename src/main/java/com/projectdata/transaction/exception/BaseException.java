package com.projectdata.transaction.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
public abstract class BaseException extends RuntimeException {
    private final HttpStatus httpStatus;
    private final String path;
    private final LocalDateTime localDateTime;

    public BaseException(String message, HttpStatus httpStatus, String path) {
        super(message);
        this.httpStatus = httpStatus;
        this.path = path;
        this.localDateTime = LocalDateTime.now();
    }
}
