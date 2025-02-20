package com.fortest.orderdelivery.app.domain.image.service;

import com.fortest.orderdelivery.app.domain.image.entity.Image;
import com.fortest.orderdelivery.app.domain.image.mapper.ImageMapper;
import com.fortest.orderdelivery.app.domain.image.repository.ImageRepository;
import com.fortest.orderdelivery.app.domain.menu.dto.MenuImageMappingRequestDto;
import com.fortest.orderdelivery.app.domain.menu.dto.MenuImageMappingResponseDto;
import com.fortest.orderdelivery.app.domain.menu.dto.MenuOptionImageMappingRequestDto;
import com.fortest.orderdelivery.app.domain.menu.dto.MenuOptionImageMappingResponseDto;
import com.fortest.orderdelivery.app.domain.menu.entity.Menu;
import com.fortest.orderdelivery.app.domain.menu.entity.MenuOption;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ImageAppService {

    private final ImageRepository imageRepository;
    private final ImageService imageService;

    // TODO : updateBy 코드 추가
    @Transactional
    public MenuImageMappingResponseDto updateMenuId(MenuImageMappingRequestDto menuOptionImageRequestDto) {
        List<String> imageIdList = menuOptionImageRequestDto.getImageIdList();
        Menu menu = menuOptionImageRequestDto.getMenu();

        List<String> updatedImageIdList = new ArrayList<>();
        boolean result = false;

        imageIdList.forEach(id -> {
            Image image = imageService.getImageById(id);
            image.updateMenu(menu);

            updatedImageIdList.add(imageRepository.save(image).getId());
        });

        if(imageIdList.size() == updatedImageIdList.size()) {
            result = true;
        }

        return ImageMapper.toMenuImageMappingResponseDto(updatedImageIdList, result);
    }

    // TODO : updateBy 코드 추가
    @Transactional
    public MenuOptionImageMappingResponseDto updateMenuOptionId(
        MenuOptionImageMappingRequestDto menuOptionImageRequestDto) {
        List<String> imageIdList = menuOptionImageRequestDto.getImageIdList();
        Menu menu = menuOptionImageRequestDto.getMenu();
        MenuOption menuOption = menuOptionImageRequestDto.getMenuOption();

        List<String> updatedImageIdList = new ArrayList<>();
        boolean result = false;

        imageIdList.forEach(id -> {
            Image image = imageService.getImageById(id);
            image.updateMenu(menu);
            image.updateOption(menuOption);

            updatedImageIdList.add(imageRepository.save(image).getId());
        });

        if(imageIdList.size() == updatedImageIdList.size()) {
            result = true;
        }

        return ImageMapper.toMenuOptionImageMappingResponseDto(updatedImageIdList, result);
    }

}
