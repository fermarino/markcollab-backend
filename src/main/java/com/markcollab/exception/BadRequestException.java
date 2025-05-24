// src/main/java/com/markcollab/exception/BadRequestException.java
package com.markcollab.exception;

import org.springframework.http.HttpStatus;

public class BadRequestException extends ApiException {
    public BadRequestException(String message) {
        super(message, HttpStatus.BAD_REQUEST.value());
    }
}
