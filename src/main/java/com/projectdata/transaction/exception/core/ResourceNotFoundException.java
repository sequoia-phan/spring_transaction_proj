package com.projectdata.transaction.exception.core;

import org.springframework.http.HttpStatus;

import com.projectdata.transaction.exception.BaseException;

public class ResourceNotFoundException extends BaseException {
    public ResourceNotFoundException(String resource, String identifier, String path) {
        super(String.format("%s with identifier %s not found", resource, identifier),
                HttpStatus.NOT_FOUND,
                path);
    }
}
