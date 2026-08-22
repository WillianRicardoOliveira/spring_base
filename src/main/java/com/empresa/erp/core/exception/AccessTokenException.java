package com.empresa.erp.core.exception;

import org.springframework.security.core.AuthenticationException;

public class AccessTokenException extends AuthenticationException {

    private static final long serialVersionUID = 1L;

    public AccessTokenException(String message) {
        super(message);
    }

    public AccessTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}