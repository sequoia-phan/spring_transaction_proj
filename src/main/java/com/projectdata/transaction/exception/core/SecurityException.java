package com.projectdata.transaction.exception.core;

import org.springframework.http.HttpStatus;

import com.projectdata.transaction.exception.BaseException;

public class SecurityException extends BaseException {
    public SecurityException(String message, String path) {
        super(message, HttpStatus.FORBIDDEN, path);
    }

    public SecurityException(String message, HttpStatus status, String path) {
        super(message, status, path);
    }
}
