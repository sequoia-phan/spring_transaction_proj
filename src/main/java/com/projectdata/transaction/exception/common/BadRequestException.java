package com.projectdata.transaction.exception.common;

import com.projectdata.transaction.exception.ApiException;
import com.projectdata.transaction.exception.BaseException;
import org.springframework.http.HttpStatus;

public class BadRequestException extends BaseException implements ApiException {

    public BadRequestException(String message, String path) {
        super(message, HttpStatus.BAD_REQUEST, path);
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}
