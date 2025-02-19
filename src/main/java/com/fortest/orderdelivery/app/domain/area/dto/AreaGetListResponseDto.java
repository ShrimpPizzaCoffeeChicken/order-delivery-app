package com.fortest.orderdelivery.app.domain.area.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AreaGetListResponseDto {

    @JsonProperty("area-list")
    private List<AreaDto> areaList;

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AreaDto {
        private String id;
        private String city;
        private String district;
        private String street;
    }
}
