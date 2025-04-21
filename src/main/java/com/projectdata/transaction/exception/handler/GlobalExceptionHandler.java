package com.projectdata.transaction.exception.handler;

import com.projectdata.transaction.exception.BaseException;
import com.projectdata.transaction.exception.core.ValidationException;
import com.projectdata.transaction.exception.model.ApiError;
import com.projectdata.transaction.exception.model.ValidationError;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ApiError> handleBaseException(BaseException ex, HttpServletRequest req) {
        String traceId = generateTraceId();
        log.error("Error occurred: {} - TraceId: {}", ex.getMessage(), traceId, ex);

        ApiError error = ApiError.builder()
                .timestamp(ex.getLocalDateTime())
                .status(ex.getHttpStatus().value())
                .error(ex.getHttpStatus().getReasonPhrase())
                .message(ex.getMessage())
                .path(ex.getPath())
                .traceId(traceId)
                .build();
        return ResponseEntity
                .status(ex.getHttpStatus())
                .body(error);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDataIntegrityViolation(DataIntegrityViolationException ex,
            HttpServletRequest req) {
        String traceId = generateTraceId();
        log.error("Data integrity violation - TraceId: {}", traceId, ex);

        ApiError error = ApiError.of(HttpStatus.CONFLICT, "Database constraint violation", req.getRequestURI());
        error.setTraceId(traceId);

        error.getDetails().put("constraint", extractConstraintName(ex));

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(error);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiError> handleValidationException(ValidationException ex, HttpServletRequest request) {
        String traceId = generateTraceId();
        log.error("Validation error: {} - TraceId: {} ", ex.getMessage(), traceId);

        ApiError error = ApiError.builder()
                .timestamp(ex.getLocalDateTime())
                .status(ex.getHttpStatus().value())
                .error(ex.getHttpStatus().getReasonPhrase())
                .message(ex.getMessage())
                .path(ex.getPath())
                .traceId(traceId)
                .validationErrors(ex.getValidationErrors())
                .build();

        return ResponseEntity.status(ex.getHttpStatus()).body(error);
    }

    private String generateTraceId() {
        return UUID.randomUUID().toString();
    }

    private String extractConstraintName(DataIntegrityViolationException ex) {
        // Add your logic to extract constraint name
        return "unique_constraint";
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationErrors(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String traceId = generateTraceId();
        log.error("Validation error - TraceId: {}", traceId, ex);

        List<ValidationError> validationErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> ValidationError.builder()
                        .field(error.getField())
                        .message(error.getDefaultMessage())
                        .build())
                .collect(Collectors.toList());

        ApiError error = ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message("Validation failed")
                .path(request.getRequestURI())
                .traceId(traceId)
                .validationErrors(validationErrors)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}
