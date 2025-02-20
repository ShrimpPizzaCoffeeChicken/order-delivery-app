package com.fortest.orderdelivery.app.domain.store.mapper;

import com.fortest.orderdelivery.app.domain.area.entity.Area;
import com.fortest.orderdelivery.app.domain.store.dto.*;
import com.fortest.orderdelivery.app.domain.store.entity.Store;

import java.util.ArrayList;
import java.util.List;

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
                .areaId(area.getId())
                .detailAddress(store.getDetailAddress())
                .ownerName(store.getOwnerName())
                .build();
    }

    public static StoreUpdateResponseDto toStoreUpdateResponseDto(Store store, Area area) {
        return StoreUpdateResponseDto.builder()
                .storeId(store.getId())
                .storeName(store.getName())
                .areaId(area.getId())
                .detailAddress(store.getDetailAddress())
                .ownerName(store.getOwnerName())
                .build();
    }

    public static StoreDeleteResponseDto toCategoryDeleteResponseDto(Store store) {
        return StoreDeleteResponseDto.builder()
                .storeId(store.getId())
                .build();
    }

    public static MenuOptionValidRequestDto storeValidReqDtoToMenuValidRedDto (StoreMenuValidRequestDto storeMenuValidRequestDto) {
        List<MenuOptionValidRequestDto.MenuDto> afterMenuDtoList = new ArrayList<>();
        List<StoreMenuValidRequestDto.MenuDto> beforeMenuDtoList = storeMenuValidRequestDto.getMenuList();
        for (StoreMenuValidRequestDto.MenuDto beforeMenuDto : beforeMenuDtoList) {
            List<MenuOptionValidRequestDto.OptionDto> afterOptionList = new ArrayList<>();
            MenuOptionValidRequestDto.MenuDto afterMenuDto = MenuOptionValidRequestDto.MenuDto.builder()
                    .id(beforeMenuDto.getId())
                    .optionList(afterOptionList)
                    .build();
            afterMenuDtoList.add(afterMenuDto);
            List<StoreMenuValidRequestDto.OptionDto> beforeOptionDtoList = beforeMenuDto.getOptionList();
            for (StoreMenuValidRequestDto.OptionDto beforeOptionDto : beforeOptionDtoList) {
                MenuOptionValidRequestDto.OptionDto afterOption = MenuOptionValidRequestDto.OptionDto.builder()
                        .id(beforeOptionDto.getId())
                        .build();
                afterOptionList.add(afterOption);
            }
        }
        return MenuOptionValidRequestDto.builder().menuList(afterMenuDtoList).build();
    }

    public static StoreMenuValidResponseDto menuOptionResDtoToStoreValidResDto (Store store, MenuOptionValidReponseDto menuOptionValidReponseDto) {
        List<StoreMenuValidResponseDto.MenuDto> afterMenuDtoList = new ArrayList<>();
        List<MenuOptionValidReponseDto.MenuDto> beforeMenuDtoList = menuOptionValidReponseDto.getMenuList();
        for (MenuOptionValidReponseDto.MenuDto beforeMenuDto : beforeMenuDtoList) {
            List<StoreMenuValidResponseDto.OptionDto> afterOptionList = new ArrayList<>();
            StoreMenuValidResponseDto.MenuDto afterMenuDto = StoreMenuValidResponseDto.MenuDto.builder()
                    .id(beforeMenuDto.getId())
                    .optionList(afterOptionList)
                    .build();
            afterMenuDtoList.add(afterMenuDto);
            List<MenuOptionValidReponseDto.OptionDto> beforeOptionDtoList = beforeMenuDto.getOptionList();
            for (MenuOptionValidReponseDto.OptionDto beforeOptionDto : beforeOptionDtoList) {
                StoreMenuValidResponseDto.OptionDto afterOption = StoreMenuValidResponseDto.OptionDto.builder()
                        .id(beforeOptionDto.getId())
                        .build();
                afterOptionList.add(afterOption);
            }
        }
        return StoreMenuValidResponseDto.builder()
                .storeId(store.getId())
                .storeName(store.getName())
                .menuList(afterMenuDtoList)
                .build();
    }
}
