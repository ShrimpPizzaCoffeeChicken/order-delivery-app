package com.fortest.orderdelivery.app.domain.menu.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fortest.orderdelivery.app.domain.menu.entity.Menu;
import com.fortest.orderdelivery.app.domain.menu.entity.MenuOption;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MenuOptionImageMappingRequestDto {
    @JsonProperty("menu-dto")
    private MenuDto menuDto;
    @JsonProperty("menu-option-dto")
    private MenuOptionDto menuOptionDto;
    @JsonProperty("image-id-list")
    private List<String> imageIdList;
}