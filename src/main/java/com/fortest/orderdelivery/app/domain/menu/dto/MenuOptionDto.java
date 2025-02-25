package com.fortest.orderdelivery.app.domain.menu.dto;

import com.fortest.orderdelivery.app.domain.menu.entity.ExposeStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MenuOptionDto {
    private String id;
    private String name;
    private String description;
    private Integer price;
    private ExposeStatus exposeStatus;
}
