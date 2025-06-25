package com.tlfdt.bonrecreme.exception.api;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApiExceptionHandler extends RuntimeException {
    private final HttpStatus status;
    private final String message;

    public ApiExceptionHandler(String runtimeMessage, HttpStatus status, String message){
        super(runtimeMessage);
        this.status = status;
        this.message = message;
    }

    public ApiExceptionHandler(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    @Override
    public String getMessage(){
        return message;
    }
}
