package com.projectdata.transaction.exception;

import org.springframework.http.HttpStatus;

public interface ApiException {
    HttpStatus getStatus();
}
