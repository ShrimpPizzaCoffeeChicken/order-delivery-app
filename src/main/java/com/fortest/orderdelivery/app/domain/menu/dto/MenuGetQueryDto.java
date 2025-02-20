package com.fortest.orderdelivery.app.domain.menu.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fortest.orderdelivery.app.domain.menu.dto.MenuGetResponseDto.OptionList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MenuGetQueryDto {

    private String menuName;
    private String menuDescription;
    private Integer menuPrice;

}
