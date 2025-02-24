package com.fortest.orderdelivery.app.domain.menu.dto;

import com.fortest.orderdelivery.app.domain.menu.entity.ExposeStatus;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MenuDto {
    private String id;
    private String name;
    private String description;
    private Integer price;
    private String storeId;
    @Builder.Default
    private List<MenuOptionDto> menuOptionList = new ArrayList<>();
    private ExposeStatus exposeStatus;
}
