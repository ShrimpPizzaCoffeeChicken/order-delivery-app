package com.fortest.orderdelivery.app.domain.menu.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MenuOptionUpdateRequestDto {
    //    @NotBlank(message = "[name:blank]")
    private String name;
    private String description;
    //    @NotBlank(message = "[price:blank]")
    private Integer price;
    @JsonProperty("expose-status")
    private String exposeStatus;
}
