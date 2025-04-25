package com.projectdata.transaction.dto.response;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class ApiResponse<T> extends BaseResponse {
    private T data;

    public static <T> ApiResponse<T> success(T data, String path, HttpStatus httpStatus) {
        return ApiResponse.<T>builder()
                .timestamp(LocalDateTime.now())
                .status(httpStatus.value())
                .message("Success")
                .path(path)
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> success(T data, String path, String message, HttpStatus httpStatus) {
        return ApiResponse.<T>builder()
                .timestamp(LocalDateTime.now())
                .status(httpStatus.value())
                .message(message)
                .path(path)
                .data(data)
                .build();
    }
    
    public static <T> ApiResponse<T> error(String message, String path, HttpStatus httpStatus) {
        return ApiResponse.<T>builder()
                .timestamp(LocalDateTime.now())
                .status(httpStatus.value())
                .message(message)
                .path(path)
                .build();
    }
}
