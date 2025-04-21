package com.projectdata.transaction.exception;

import org.springframework.http.HttpStatus;
import java.time.LocalDateTime;

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

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getPath() {
        return path;
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }
}
