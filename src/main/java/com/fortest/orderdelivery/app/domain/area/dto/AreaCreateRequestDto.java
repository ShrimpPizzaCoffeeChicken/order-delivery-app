package com.fortest.orderdelivery.app.domain.area.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AreaCreateRequestDto {
    private String city;
    private String district;
    private String street;
}
