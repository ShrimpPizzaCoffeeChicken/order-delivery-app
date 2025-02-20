package com.fortest.orderdelivery.app.global.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CommonUtil {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static String LDTToString(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return "";
        }
        return localDateTime.format(formatter);
    }

    // 빈문자열 or null 이면 true
    public static boolean checkStringIsEmpty (String target) {
        return target == null || target.isEmpty();
    }
}
