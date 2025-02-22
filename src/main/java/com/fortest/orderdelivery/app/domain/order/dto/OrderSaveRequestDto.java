package com.fortest.orderdelivery.app.domain.order.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderSaveRequestDto {

    @Size(min = 1, max = 50, message = "store-id 는 필수 입력값입니다.")
    @JsonProperty("store-id")
    private String storeId;

    @Pattern(regexp = "DELIVERY|INSTORE", message = "order-type 은 DELIVERY, INSTORE 만 허용합니다.")
    @JsonProperty("order-type")
    private String orderType;

    @JsonProperty("menu-list")
    private List<MenuDto> menuList = new ArrayList<>();

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MenuDto {
        @Size(min = 1, max = 50, message = "menu-list[].id 는 50자 이하만 입력가능합니다.")
        private String id;
        @Positive(message = "count 는 양의 정수만 입력 가능합니다.")
        private int count;
        @JsonProperty("option-list")
        private List<OptionDto> optionList = new ArrayList<>();
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OptionDto {
        @Size(min = 1, max = 50, message = "order-list[].id 는 50자 이하만 입력가능합니다.")
        private String id;
        @Positive(message = "count 는 양의 정수만 입력 가능합니다.")
        private int count;
    }
}
