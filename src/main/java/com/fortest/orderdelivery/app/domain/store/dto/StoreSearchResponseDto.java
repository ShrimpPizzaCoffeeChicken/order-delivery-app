package com.fortest.orderdelivery.app.domain.store.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StoreSearchResponseDto {

    private String search;

    @JsonProperty("total-contents")
    private Long totalContents;

    private Integer size;

    @JsonProperty("current-page")
    private Integer currentPage;

    @JsonProperty("store-list")
    private List<storeDto> storeList;

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class storeDto {
        @JsonProperty("store-id")
        private String storeId;
        @JsonProperty("store-name")
        private String storeName;
        private String area;
        @JsonProperty("detail-address")
        private String detailAddress;
        @JsonProperty("owner-name")
        private String ownerName;
    }
}
