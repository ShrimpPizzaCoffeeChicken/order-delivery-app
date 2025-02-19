package com.fortest.orderdelivery.app.global.exception;

public class NotValidRequestException extends RuntimeException{
    public NotValidRequestException(String message) {
        super(message);
    }
}
