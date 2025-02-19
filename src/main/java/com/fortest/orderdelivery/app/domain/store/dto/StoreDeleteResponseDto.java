package com.fortest.orderdelivery.app.domain.store.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public class StoreDeleteResponseDto {
    @JsonProperty("store-id")
    private String storeId;

}
