package com.markcollab.exception;

import lombok.Getter;

@Getter
public abstract class ApiException extends RuntimeException {
    private final int status;
    public ApiException(String message, int status) { super(message); this.status = status; }
}
