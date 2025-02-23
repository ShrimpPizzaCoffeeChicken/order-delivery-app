package com.fortest.orderdelivery.app.global.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fortest.orderdelivery.app.global.exception.BusinessLogicException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "CommonUtil")
public class CommonUtil {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String LDTToString(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return "";
        }
        return localDateTime.format(formatter);
    }

    public static LocalDateTime stringToLDT (String ldtString) {
        log.info("ldtString : {}", ldtString);
        if(Objects.isNull(ldtString) || ldtString.isBlank()) {
            return null;
        }
        return LocalDateTime.parse(ldtString, formatter);
    }

    // 빈문자열 or null 이면 true
    public static boolean checkStringIsEmpty (String target) {
        return target == null || target.isEmpty();
    }

    public static <T> T convertJsonToDto(String jsonString, Class<T> classType) {
        try {
            return objectMapper.readValue(jsonString, classType);
        } catch (Exception e) {
            throw new BusinessLogicException("Json 변환 실패");
        }
    }
}
