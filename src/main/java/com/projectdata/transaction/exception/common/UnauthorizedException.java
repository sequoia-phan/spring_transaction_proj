package com.projectdata.transaction.exception.common;

import com.projectdata.transaction.exception.ApiException;
import com.projectdata.transaction.exception.BaseException;
import org.springframework.http.HttpStatus;

public class UnauthorizedException extends BaseException implements ApiException {

    public UnauthorizedException(String message, String path) {
        super(message, HttpStatus.UNAUTHORIZED, path);
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.UNAUTHORIZED;
    }
}
