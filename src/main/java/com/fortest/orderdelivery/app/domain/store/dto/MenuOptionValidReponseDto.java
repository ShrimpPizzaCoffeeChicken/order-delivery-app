package com.fortest.orderdelivery.app.domain.store.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuOptionValidReponseDto {

    private Boolean result;

    @JsonProperty("menu-list")
    private List<MenuDto> menuList;

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MenuDto {
        private String id;
        private Integer price;
        private String name;
        @JsonProperty("option-list")
        private List<OptionDto> optionList;
    }

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OptionDto {
        private Integer price;
        private String name;
        private String id;
    }
}
