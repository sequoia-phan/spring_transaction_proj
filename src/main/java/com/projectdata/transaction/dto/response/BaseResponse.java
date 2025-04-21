package com.projectdata.transaction.dto.response;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class BaseResponse {
    private LocalDateTime timestamp;
    private HttpStatus status;
    private String message;
    private String path;
    private String traceId;
}
