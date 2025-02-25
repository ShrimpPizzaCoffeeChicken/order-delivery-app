package com.fortest.orderdelivery.app.domain.store.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@ToString // 로깅을 위해 추가
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class StoreMenuValidRequestDto {

    @JsonProperty("menu-list")
    private List<MenuDto> menuList;

    @ToString // 로깅을 위해 추가
    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MenuDto {
        @Size(min = 1, max = 50, message = "메뉴 ID는 필수 입력값입니다.")
        private String id;
        @JsonProperty("option-list")
        private List<OptionDto> optionList;
    }

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OptionDto {
        @Size(min = 1, max = 50, message = "메뉴 옵션 ID는 필수 입력값입니다.")
        private String id;
    }
}
