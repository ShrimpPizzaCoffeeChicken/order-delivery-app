package com.fortest.orderdelivery.app.domain.area.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AreaSaveRequestDto {
    @Size(min = 1, max = 50)
    private String city;
    @Size(min = 1, max = 50)
    private String district;
    @Size(min = 1, max = 50)
    private String street;
}
