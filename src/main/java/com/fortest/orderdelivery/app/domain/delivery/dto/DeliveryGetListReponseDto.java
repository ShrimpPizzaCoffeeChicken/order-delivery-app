package com.fortest.orderdelivery.app.domain.delivery.dto;

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
public class DeliveryGetListReponseDto {

    @JsonProperty("total-contents")
    private Long totalContents;
    private String search;
    private Integer size;
    @JsonProperty("current-page")
    private Integer currentPage;
    @JsonProperty("delivery-list")
    private List<DeliveryDto> deliveryList;

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DeliveryDto {
        @JsonProperty("delivery-id")
        private String deliveryId;
        private String address;
        private String status;
    }
}
