package com.fortest.orderdelivery.app.domain.menu.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MenuImageMappingRequestDto {
    private String menuId;
    private List<String> imageIdList;
}
