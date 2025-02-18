package com.fortest.orderdelivery.app.domain.store.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class StoreUpdateRequestDto {
    @JsonProperty("store-name")
    private String storeName;

    @JsonProperty("area-id")
    private String areaId;

    @JsonProperty("detail-address")
    private String detailAddress;

    @JsonProperty("owner-name")
    private String ownerName;
}
