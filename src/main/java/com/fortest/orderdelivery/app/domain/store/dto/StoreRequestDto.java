package com.fortest.orderdelivery.app.domain.store.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class StoreRequestDto {

    @JsonProperty("store-id")
    private String storeId;

    @JsonProperty("store-name")
    private String storeName;

    @JsonProperty("area-id")
    private String areaId;

    @JsonProperty("detail-address")
    private String detailAddress;

    @JsonProperty("owner-name")
    private String ownerName;
}
