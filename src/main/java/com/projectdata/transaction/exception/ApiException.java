package com.projectdata.transaction.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public interface ApiException {
    HttpStatus getStatus();

    String path(HttpServletRequest request);

    default LocalDateTime getTimestamp() {
        return LocalDateTime.now();
    }
}
