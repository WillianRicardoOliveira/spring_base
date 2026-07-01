package com.empresa.erp.core.exception;

public class SsoAuthenticationException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

    public SsoAuthenticationException(String message) {
        super(message);
    }

    public SsoAuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}