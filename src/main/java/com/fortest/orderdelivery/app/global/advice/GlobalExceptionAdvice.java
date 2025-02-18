package com.fortest.orderdelivery.app.global.advice;

import com.fortest.orderdelivery.app.global.dto.CommonDto;
import com.fortest.orderdelivery.app.global.exception.BusinessLogicException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@CrossOrigin
@Slf4j
@RestControllerAdvice
public class GlobalExceptionAdvice {

    // 유효성 검사 실패 : MethodArgumentNotValidException
    @ExceptionHandler
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public CommonDto<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {

        // 메세지 구성
        String message = e.getMessage();

        return CommonDto.builder()
                .message(message)
                .code(HttpStatus.METHOD_NOT_ALLOWED.value())
                .data(null)
                .build();
    }

    // 유효성 검사 실패 : ConstraintViolationException
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CommonDto<Object> handleConstraintViolationException(ConstraintViolationException e) {

        // 메세지 구성
        String message = e.getMessage();

        return CommonDto.builder()
                .message(message)
                .code(HttpStatus.METHOD_NOT_ALLOWED.value())
                .data(null)
                .build();
    }

    // 요청 포멧 문제로 인해 HttpMessageNotReadableException 발생 시
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CommonDto<Object> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException e) {

        // 메세지 구성
        String message = e.getMessage();

        return CommonDto.builder()
                .message(message)
                .code(HttpStatus.METHOD_NOT_ALLOWED.value())
                .data(null)
                .build();
    }

    // http method 잘못 호출로 인한 HttpRequestMethodNotSupportedException 발생 시
    @ExceptionHandler
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public CommonDto<Object> handleHttpRequestMethodNotSupportedException(
            HttpRequestMethodNotSupportedException e) {

        // 메세지 구성
        String message = e.getMessage();

        return CommonDto.builder()
                .message(message)
                .code(HttpStatus.METHOD_NOT_ALLOWED.value())
                .data(null)
                .build();
    }

    // 컨트롤러에서 지정한 필수 파라미터 부재로 인한 MissingServletRequestParameterException 발생 시
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CommonDto<Object> handleMissingServletRequestParameterException(
            MissingServletRequestParameterException e) {

        // 메세지 구성
        String message = e.getMessage();

        return CommonDto.builder()
                .message(message)
                .code(HttpStatus.METHOD_NOT_ALLOWED.value())
                .data(null)
                .build();
    }

    // BusinessLogicException 발생시
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CommonDto<Object> handleBusinessLogicException(BusinessLogicException e) {

        String message = e.getMessage();

        return CommonDto.builder()
                .message(message)
                .code(HttpStatus.METHOD_NOT_ALLOWED.value())
                .data(null)
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CommonDto<Object> handleIllegalArgumentException(IllegalArgumentException e) {

        String message = e.getMessage();

        return CommonDto.builder()
            .message(message)
            .code(HttpStatus.METHOD_NOT_ALLOWED.value())
            .data(null)
            .build();
    }
}
