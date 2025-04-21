package com.projectdata.transaction.exception.core;

import java.util.List;

import org.springframework.http.HttpStatus;

import com.projectdata.transaction.exception.BaseException;
import com.projectdata.transaction.exception.model.ValidationError;

public class ValidationException extends BaseException {
    private final List<ValidationError> errors;

    public ValidationException(String message, String path, List<ValidationError> errors) {
        super(message, HttpStatus.BAD_REQUEST, path);
        this.errors = errors;
    }

    public List<ValidationError> getValidationErrors() {
        return errors;
    }
}
