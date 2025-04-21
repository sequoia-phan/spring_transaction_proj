package com.projectdata.transaction.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;

import com.projectdata.transaction.exception.model.ValidationError;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class ErrorResponse extends BaseResponse {
    private List<ValidationError> validationErrors;
    private Map<String, Object> details;

    public static ErrorResponse of(HttpStatus status, String message, String path) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status)
                .message(message)
                .path(path)
                .build();
    }

    public static ErrorResponse of(HttpStatus status, String message, String path, List<ValidationError> errors) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status)
                .message(message)
                .path(path)
                .validationErrors(errors)
                .build();
    }
}
