package com.fortest.orderdelivery.app.global.util;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

@RequiredArgsConstructor
@Component
public class MessageUtil {

    private final MessageSource messageSource;
    private static final Locale locale = Locale.KOREA;

    public String getMessage(String code) {
        return messageSource.getMessage(code, null, locale);
    }

    public String getMessage(String code, String ... args) {
        return messageSource.getMessage(code, args, locale);
    }
}
