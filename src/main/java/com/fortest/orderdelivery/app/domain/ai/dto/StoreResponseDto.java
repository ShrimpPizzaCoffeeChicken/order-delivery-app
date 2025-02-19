package com.fortest.orderdelivery.app.domain.ai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StoreResponseDto {

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
