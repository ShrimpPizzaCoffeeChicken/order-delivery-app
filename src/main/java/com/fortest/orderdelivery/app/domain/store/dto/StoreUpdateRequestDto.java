package com.fortest.orderdelivery.app.domain.store.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class StoreUpdateRequestDto {

    @Size(min = 1, max = 100, message = "가게 이름은 필수 입력값입니다.")
    @JsonProperty("store-name")
    private String storeName;

    @Size(min = 1, max = 100)
    @JsonProperty("area-id")
    private String areaId;

    @Size(min = 1, max = 200)
    @JsonProperty("detail-address")
    private String detailAddress;

    @Size(min = 1, max = 100)
    @JsonProperty("owner-name")
    private String ownerName;
}
