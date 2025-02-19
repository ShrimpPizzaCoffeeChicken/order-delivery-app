package com.fortest.orderdelivery.app.domain.image.dto;

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
public class MenuImageResponseDto {
    @JsonProperty("image-id-list")
    List<String> imageIdList;
}
