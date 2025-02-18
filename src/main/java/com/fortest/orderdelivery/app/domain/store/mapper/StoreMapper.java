package com.fortest.orderdelivery.app.domain.store.mapper;

import com.fortest.orderdelivery.app.domain.area.entity.Area;
import com.fortest.orderdelivery.app.domain.store.dto.StoreSaveRequestDto;
import com.fortest.orderdelivery.app.domain.store.dto.StoreSaveResponseDto;
import com.fortest.orderdelivery.app.domain.store.dto.StoreUpdateResponseDto;
import com.fortest.orderdelivery.app.domain.store.entity.Store;

public class StoreMapper {

    public static Store toStore(StoreSaveRequestDto storeSaveRequestDto, Area area) {
        return Store.builder()
                .name(storeSaveRequestDto.getStoreName())
                .area(area)
                .detailAddress(storeSaveRequestDto.getDetailAddress())
                .ownerName(storeSaveRequestDto.getOwnerName())
                .build();
    }

    public static StoreSaveResponseDto toStoreSaveResponseDto(Store store, Area area) {
        return StoreSaveResponseDto.builder()
                .storeId(store.getId())
                .storeName(store.getName())
                .areaId(area.getAreaId())
                .detailAddress(store.getDetailAddress())
                .ownerName(store.getOwnerName())
                .build();
    }

    public static StoreUpdateResponseDto toStoreUpdateResponseDto(Store store, Area area) {
        return StoreUpdateResponseDto.builder()
                .storeId(store.getId())
                .storeName(store.getName())
                .areaId(area.getAreaId())
                .detailAddress(store.getDetailAddress())
                .ownerName(store.getOwnerName())
                .build();
    }
}
