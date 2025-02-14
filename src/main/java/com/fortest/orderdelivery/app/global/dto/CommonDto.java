package com.fortest.orderdelivery.app.global.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommonDto<T> {
    private String message;
    private Integer code;
    private T data;
}
