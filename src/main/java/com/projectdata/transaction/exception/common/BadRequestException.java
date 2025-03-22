package com.projectdata.transaction.exception.common;

import com.projectdata.transaction.exception.ApiException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;
import org.springframework.http.HttpStatus;

public class BadRequestException implements ApiException {

    private String message;

    public BadRequestException(String message) {
        this.message = message;
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.BAD_REQUEST;
    }

    @Override
    public String path(@NonNull HttpServletRequest request) {
        return request.getRequestURI();
    }
}
