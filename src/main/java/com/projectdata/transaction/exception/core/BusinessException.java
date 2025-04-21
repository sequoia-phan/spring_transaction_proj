package com.projectdata.transaction.exception.core;

import org.springframework.http.HttpStatus;

import com.projectdata.transaction.exception.BaseException;

public class BusinessException extends BaseException {
    public BusinessException(String message, String path) {
        super(message, HttpStatus.UNPROCESSABLE_ENTITY, path);
    }

    public BusinessException(String message, HttpStatus httpStatus, String path) {
        super(message, httpStatus, path);
    }
}
