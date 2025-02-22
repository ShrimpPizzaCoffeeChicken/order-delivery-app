package com.fortest.orderdelivery.app.domain.store.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuOptionValidRequestDto {

    @Size(min = 1, max = 50, message = "가게 ID는 필수 입력값입니다.")
    @JsonProperty("store-id")
    private String storeId;

    @JsonProperty("menu-list")
    private List<MenuDto> menuList;

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MenuDto {
        private String id;
        @JsonProperty("option-list")
        private List<OptionDto> optionList;
    }

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OptionDto {
        private String id;
    }
}
