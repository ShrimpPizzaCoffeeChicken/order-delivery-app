package com.fortest.orderdelivery.app.domain.store.dto;

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

    private List<String> addCategoryIdList;

    private List<String> deleteCategoryIdList;
}
