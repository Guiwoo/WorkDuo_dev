package com.workduo.error.global.exception;

public class UsernameFromTokenException extends RuntimeException{
    public UsernameFromTokenException(String message){
        super(message);
    }
}
