package com.projectdata.transaction.exception.handler;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.projectdata.transaction.exception.model.ApiError;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SecurityExceptionHandler {
    @ExceptionHandler({ AccessDeniedException.class, BadCredentialsException.class })
    public ResponseEntity<ApiError> handleAccessDenied(Exception ex,
            HttpServletRequest req) {
        String traceId = generateTraceId();
        log.error("Access denied: {} - TraceId: {}", ex.getMessage(), traceId, ex);

        ApiError error = ApiError.of(HttpStatus.FORBIDDEN,
                "Access denied: " + ex.getMessage(),
                req.getRequestURI());

        error.setTraceId(traceId);

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(error);
    }

    private String generateTraceId() {
        return UUID.randomUUID().toString();
    }
}
