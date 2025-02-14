package com.fortest.orderdelivery.app.domain.menu.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ExposeStatus {
    ONSALE("판매중"),
    SOLDOUT("품절"),
    HIDING("숨김");

    private String message;
}
