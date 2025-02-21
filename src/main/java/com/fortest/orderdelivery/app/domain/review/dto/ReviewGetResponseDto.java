package com.fortest.orderdelivery.app.domain.review.dto;

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
public class ReviewGetResponseDto {

    @JsonProperty("store-id")
    private String storeId;

    @JsonProperty("store-name")
    private String storeName;

    private String contents;

    private Long rate;

    @JsonProperty("created-at")
    private String createdAt;

    @JsonProperty("updated-at")
    private String updatedAt;

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
