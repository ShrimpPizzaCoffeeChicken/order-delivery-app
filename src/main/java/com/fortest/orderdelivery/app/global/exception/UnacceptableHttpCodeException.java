package com.fortest.orderdelivery.app.global.exception;

public class UnacceptableHttpCodeException extends RuntimeException {
    public UnacceptableHttpCodeException(String message) {
        super(message);
    }
}
