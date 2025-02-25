package com.fortest.orderdelivery.app.domain.menu.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MenuOptionUpdateRequestDto {

    @Size(min = 1, max = 100, message = "메뉴 옵션 이름은 필수 입력값입니다.")
    private String name;

    private String description;

    @Positive(message = "price 는 양의 정수만 입력 가능합니다.")
    private Integer price;

    @Pattern(regexp = "ONSALE|SOLDOUT|HIDING", message = "menu-option-expose-status는 ONSALE, SOLDOUT, HIDING 만 허용합니다.")
    @JsonProperty("expose-status")
    private String exposeStatus;
}
