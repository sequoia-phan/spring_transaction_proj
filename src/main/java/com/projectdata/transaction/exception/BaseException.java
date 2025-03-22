package com.projectdata.transaction.exception;

import org.springframework.http.HttpStatus;

public class BaseException extends RuntimeException {
    private ApiException apiExcept;

    public BaseException(String msg, ApiException exception) {
        super(msg);
        this.apiExcept = exception;
    }

    public HttpStatus status(){
        return this.apiExcept.getStatus();
    }
}
