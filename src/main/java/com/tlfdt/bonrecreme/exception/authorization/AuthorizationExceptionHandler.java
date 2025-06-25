package com.tlfdt.bonrecreme.exception.authorization;

public class AuthorizationExceptionHandler extends RuntimeException {
    public AuthorizationExceptionHandler(String message) {
        super(message);
    }
}
