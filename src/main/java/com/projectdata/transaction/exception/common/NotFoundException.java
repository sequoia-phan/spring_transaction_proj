package com.projectdata.transaction.exception.common;

import com.projectdata.transaction.exception.ApiException;
import com.projectdata.transaction.exception.BaseException;
import org.springframework.http.HttpStatus;

public class NotFoundException extends BaseException implements ApiException {

    public NotFoundException(String message, String path) {
        super(message, HttpStatus.NOT_FOUND, path);
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.NOT_FOUND;
    }

}
