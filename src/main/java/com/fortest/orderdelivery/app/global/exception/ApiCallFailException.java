package com.fortest.orderdelivery.app.global.exception;

public class ApiCallFailException extends RuntimeException {
    public ApiCallFailException(String message) {
        super(message);
    }
}
