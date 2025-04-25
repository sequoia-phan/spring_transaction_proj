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
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiError> handleNoHandlerFoundException(NoHandlerFoundException ex,
            HttpServletRequest request) {
        ApiError apiError = ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                .message("The requested resource was not found")
                .path(request.getRequestURI())
                .traceId(generateTraceId())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex,
            HttpServletRequest request) {
        String paramName = ex.getName();
        String invalidValue = ex.getValue() != null ? ex.getValue().toString() : "null";
        String message = String.format("Parameter '%s' with value '%s' is invalid", paramName, invalidValue);

        List<ValidationError> validationErrors = List.of(
                ValidationError.builder()
                        .field(paramName)
                        .message("Invalid parameter type")
                        .build());

        ApiError apiError = ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(message)
                .path(request.getRequestURI())
                .traceId(generateTraceId())
                .validationErrors(validationErrors)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
    }

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
    public ResponseEntity<ApiError> handleValidationErrors(MethodArgumentNotValidException ex,
            HttpServletRequest request) {
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
