package com.fortest.orderdelivery.app.domain.menu.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MenuGetResponseDto {
    @JsonProperty("menu-name")
    private String menuName;
    @JsonProperty("menu-description")
    private String menuDescription;
    @JsonProperty("menu-price")
    private Integer menuPrice;
    @JsonProperty("menu-image-url")
    private List<String> menuImageUrl;
    @JsonProperty("option-list")
    private List<OptionList> optionList;

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OptionList {
        @JsonProperty("option-name")
        private String optionName;
        @JsonProperty("option-price")
        private Integer optionPrice;
    }

    public void updateMenuImageUrl(List<String> menuImageUrl) {
        this.menuImageUrl = menuImageUrl;
    }
}
