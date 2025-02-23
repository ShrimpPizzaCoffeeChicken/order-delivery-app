package com.fortest.orderdelivery.app.domain.ai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AiRequestGetListRequestDto {

    @Size(min = 1, max = 50)
    @JsonProperty("store-id")
    private String storeId;

    @Positive(message = "page 값은 음수일 수 없습니다.")
    private Integer page;

    @Positive(message = "page 값은 음수일 수 없습니다.")
    private Integer size;

    @Pattern(regexp = "CREATED|UPDATED", message = "orderby 는 CREATED, UPDATED 만 허용합니다.")
    private String orderby;

    private String sort;

    @Size(max = 100)
    private String search;

}
