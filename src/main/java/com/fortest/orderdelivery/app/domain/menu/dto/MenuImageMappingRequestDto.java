package com.fortest.orderdelivery.app.domain.menu.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fortest.orderdelivery.app.domain.menu.entity.Menu;
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
    @JsonProperty("menu")
    private Menu menu;
    @JsonProperty("image-id-list")
    private List<String> imageIdList;
}
