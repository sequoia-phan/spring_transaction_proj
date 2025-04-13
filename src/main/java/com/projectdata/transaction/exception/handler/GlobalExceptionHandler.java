package com.projectdata.transaction.exception.handler;

import com.projectdata.transaction.exception.BaseException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<Map<String, Object>> handleBaseException(@NonNull BaseException ex, HttpServletRequest req) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("status", ex.getHttpStatus().value());
        errorResponse.put("error", ex.getHttpStatus().getReasonPhrase());
        errorResponse.put("message", ex.getMessage());
        errorResponse.put("timestamp", ex.getLocalDateTime());
        errorResponse.put("path", ex.getPath());
        return new ResponseEntity<>(errorResponse, ex.getHttpStatus());
    }

}
