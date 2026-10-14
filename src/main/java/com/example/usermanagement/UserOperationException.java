package com.example.usermanagement;

import org.springframework.http.HttpStatus;

public class UserOperationException extends RuntimeException {
    private final HttpStatus status;

    public UserOperationException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
