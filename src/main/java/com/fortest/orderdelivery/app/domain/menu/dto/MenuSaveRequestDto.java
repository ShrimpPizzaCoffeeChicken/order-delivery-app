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
public class MenuSaveRequestDto {
//    @NotBlank(message = "[storeId:blank]")
    private String storeId;
//    @NotBlank(message = "[name:blank]")
    private String name;
    private String description;
//    @NotBlank(message = "[price:blank]")
    private Integer price;
    private String exposeStatus;
    private List<String> imageIdList;
}
