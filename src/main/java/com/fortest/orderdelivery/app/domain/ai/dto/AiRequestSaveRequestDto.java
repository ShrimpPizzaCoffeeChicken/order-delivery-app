package com.fortest.orderdelivery.app.domain.ai.dto;

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

    @Size(min = 1, max = 50)
    private String storeId;

    private String question;

}
