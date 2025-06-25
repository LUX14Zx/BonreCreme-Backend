package com.tlfdt.bonrecreme.exception.jwt;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class JwtFailException extends RuntimeException{

    private final HttpStatus status;
    private final String message;

    public JwtFailException(HttpStatus status, String message){
        this.status = status;
        this.message = message;
    }

    public JwtFailException(String runtimeMsg, HttpStatus status, String message){
        super(runtimeMsg);
        this.status = status;
        this.message = message;
    }

    @Override
    public String getMessage(){
        return message;
    }
}
