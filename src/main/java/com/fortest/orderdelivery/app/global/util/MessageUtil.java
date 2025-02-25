package com.fortest.orderdelivery.app.global.util;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

@RequiredArgsConstructor
@Component
public class MessageUtil {

    private final MessageSource messageSource;
    private static final Locale locale = Locale.KOREA;

    @Value("${app.message.success}")
    private String successMessage;

    public String getSuccessMessage () {
        return this.successMessage;
    }

    public String getMessage(String code) {
        return messageSource.getMessage(code, null, locale);
    }

    public String getMessage(String code, String ... args) {
        return messageSource.getMessage(code, args, locale);
    }
}
