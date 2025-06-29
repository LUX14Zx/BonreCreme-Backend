package com.tlfdt.bonrecreme.exception.custom;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT) // This will result in a 409 Conflict HTTP status
public class RegistrationException extends RuntimeException {
    public RegistrationException(String message) {
        super(message);
    }
}