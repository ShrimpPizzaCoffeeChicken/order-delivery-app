package com.fortest.orderdelivery.app.domain.menu.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty("menu-dto")
    private MenuDto menuDto;
    @JsonProperty("image-id-list")
    private List<String> imageIdList;
}
