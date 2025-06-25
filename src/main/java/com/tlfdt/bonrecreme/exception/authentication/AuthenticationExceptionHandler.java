package com.tlfdt.bonrecreme.exception.authentication;

public class AuthenticationExceptionHandler extends RuntimeException {
    public AuthenticationExceptionHandler(String message) {
        super(message);
    }
}
