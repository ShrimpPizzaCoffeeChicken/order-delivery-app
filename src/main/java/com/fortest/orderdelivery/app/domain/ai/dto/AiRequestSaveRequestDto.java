package com.fortest.orderdelivery.app.domain.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AiRequestSaveRequestDto {
    private String storeId;
    private String question;
}
