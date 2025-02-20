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
public class MenuOptionsSaveRequestDto {

    private String name;
    private String description;
    private Integer price;
    @JsonProperty("expose-status")
    private String exposeStatus;
    @JsonProperty("image-id-list")
    private List<String> imageIdList;
}
