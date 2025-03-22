package com.projectdata.transaction.exception.common;

import com.projectdata.transaction.exception.ApiException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;
import org.springframework.http.HttpStatus;

public class UnauthorizedException implements ApiException {
    private String message;

    public UnauthorizedException(String message) {
        this.message = message;
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.UNAUTHORIZED;
    }

    @Override
    public String path(@NonNull HttpServletRequest request) {
        return request.getRequestURI();
    }
}
