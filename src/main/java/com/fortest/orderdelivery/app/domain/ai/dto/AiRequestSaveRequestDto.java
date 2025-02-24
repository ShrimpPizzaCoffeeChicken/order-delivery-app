package com.fortest.orderdelivery.app.domain.ai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AiRequestSaveRequestDto {

    @JsonProperty("store-id")
    @Size(min = 1, max = 50)
    private String storeId;

    private String question;

}
