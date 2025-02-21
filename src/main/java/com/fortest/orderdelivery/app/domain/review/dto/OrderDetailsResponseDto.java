package com.fortest.orderdelivery.app.domain.review.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fortest.orderdelivery.app.domain.review.dto.ReviewGetResponseDto.MenuDto;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetailsResponseDto {

    @JsonProperty("menu-list")
    private List<ReviewGetResponseDto.MenuDto> menuList;

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MenuDto {
        @JsonProperty("menu-name")
        private String menuName;
        @JsonProperty("menu-count")
        private Integer menuCount;
        @JsonProperty("order-list")
        private List<ReviewGetResponseDto.OptionDto> orderList;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OptionDto {
        @JsonProperty("order-name")
        private String optionName;
        @JsonProperty("order-count")
        private Integer optionCount;
    }
}
