package com.projectdata.transaction.exception.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ValidationError {
    private String field;
    private String message;
}