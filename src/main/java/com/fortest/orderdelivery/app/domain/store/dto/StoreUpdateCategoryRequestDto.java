package com.fortest.orderdelivery.app.domain.store.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class StoreUpdateCategoryRequestDto {

    @JsonProperty("add-category-id-list")
    private List<String> addCategoryIdList;
    @JsonProperty("delete-category-id-list")
    private List<String> deleteCategoryIdList;
}
