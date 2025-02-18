package com.fortest.orderdelivery.app.domain.image.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MenuImageSaveResponseDto {
    List<String> imageIdList;
}
