package com.fortest.orderdelivery.app.domain.menu.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuImageMappingResponseDto {
    private Boolean result;
    private List<String> imageIdList;
}
