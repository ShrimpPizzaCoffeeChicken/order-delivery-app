package com.fortest.orderdelivery.app.domain.menu.service;

import com.fortest.orderdelivery.app.domain.menu.dto.MenuGetResponseDto;
import com.fortest.orderdelivery.app.domain.menu.dto.MenuImageMappingRequestDto;
import com.fortest.orderdelivery.app.domain.menu.dto.MenuImageMappingResponseDto;
import com.fortest.orderdelivery.app.domain.menu.dto.MenuListGetRequestDto;
import com.fortest.orderdelivery.app.domain.menu.dto.MenuListGetResponseDto;
import com.fortest.orderdelivery.app.domain.menu.dto.MenuListGetResponseDto.MenuListDto;
import com.fortest.orderdelivery.app.domain.menu.dto.MenuListSearchRequestDto;
import com.fortest.orderdelivery.app.domain.menu.dto.MenuResponseDto;
import com.fortest.orderdelivery.app.domain.menu.dto.MenuSaveRequestDto;
import com.fortest.orderdelivery.app.domain.menu.dto.MenuUpdateRequestDto;
import com.fortest.orderdelivery.app.domain.menu.entity.ExposeStatus;
import com.fortest.orderdelivery.app.domain.menu.entity.Menu;
import com.fortest.orderdelivery.app.domain.menu.mapper.MenuMapper;
import com.fortest.orderdelivery.app.domain.menu.repository.MenuQueryRepository;
import com.fortest.orderdelivery.app.domain.menu.repository.MenuRepository;
import com.fortest.orderdelivery.app.domain.user.entity.User;
import com.fortest.orderdelivery.app.global.exception.BusinessLogicException;
import com.fortest.orderdelivery.app.global.exception.NotFoundException;
import com.fortest.orderdelivery.app.global.gateway.ApiGateway;
import com.fortest.orderdelivery.app.global.util.JpaUtil;
import com.fortest.orderdelivery.app.global.util.MessageUtil;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j(topic = "MenuService")
@Service
@RequiredArgsConstructor
public class MenuService {

    private final ApiGateway apiGateway;
    private final MessageUtil messageUtil;
    private final MenuRepository menuRepository;
    private final MenuQueryRepository menuQueryRepository;

    public MenuResponseDto saveMenu(MenuSaveRequestDto menuSaveRequestDto, User user) {
        String storeId = menuSaveRequestDto.getStoreId();

        apiGateway.getValidStoreFromApp(storeId, user);

        Menu newMenu = MenuMapper.toMenu(menuSaveRequestDto);
        newMenu.isCreatedBy(user.getId());
        Menu savedMenu = menuRepository.save(newMenu);

        List<String> imageIdList = menuSaveRequestDto.getImageIdList();

        if (!Objects.isNull(imageIdList)) {
            MenuImageMappingRequestDto menuImageRequestDto = MenuImageMappingRequestDto.builder()
                .imageIdList(imageIdList)
                .menuDto(MenuMapper.toMenuDto(savedMenu))
                .build();

            MenuImageMappingResponseDto commonDto = apiGateway.saveMenuIdToImage(
                menuImageRequestDto, user);

            if (!commonDto.getResult()) {
                throw new BusinessLogicException(
                    messageUtil.getMessage("image.menu.mapping.failure"));
            }
        }

        return MenuMapper.toMenuResponseDto(savedMenu);
    }

    public MenuListGetResponseDto getMenuList(MenuListGetRequestDto menuListGetRequestDto) {
        PageRequest pageRequest = JpaUtil.getNormalPageable(
            menuListGetRequestDto.getPage(),
            menuListGetRequestDto.getSize(),
            menuListGetRequestDto.getOrderby(),
            menuListGetRequestDto.getSort()
        );
        Page<MenuListDto> menuListPage = menuQueryRepository.getMenuListPage(pageRequest, menuListGetRequestDto.getStoreId());

        return MenuMapper.toMenuListGetResponseDto(menuListPage);
    }

    @Transactional
    public MenuResponseDto updateMenu(MenuUpdateRequestDto menuUpdateRequestDto, String menuId,
        User user) {
        Menu menu = getMenuById(menuId);

        menu.updateMenu(
            menuUpdateRequestDto.getName(),
            menuUpdateRequestDto.getDescription(),
            menuUpdateRequestDto.getPrice(),
            ExposeStatus.valueOf(menuUpdateRequestDto.getExposeStatus())
        );
        menu.isUpdatedNow(user.getId());

        Menu savedMenu = menuRepository.save(menu);

        return MenuMapper.toMenuResponseDto(savedMenu);
    }

    @Transactional
    public MenuResponseDto deleteMenu(String menuId, User user) {
        apiGateway.deleteMenuImageFromApp(menuId, user);

        Menu menu = getMenuById(menuId);
        menu.isDeletedNow(user.getId());
        Menu savedMenu = menuRepository.save(menu);

        return MenuMapper.toMenuResponseDto(savedMenu);
    }

    public MenuGetResponseDto getMenu(String menuId) {
        MenuGetResponseDto menuGetResponseDto = menuQueryRepository.getMenuDetails(menuId);

        if (Objects.isNull(menuGetResponseDto)) {
            throw new NotFoundException(
                messageUtil.getMessage("not-found.menu"));
        }

        return menuGetResponseDto;
    }

    public MenuListGetResponseDto searchMenuList(String storeId, MenuListSearchRequestDto menuListSearchRequestDto) {
        PageRequest pageRequest = JpaUtil.getNormalPageable(
            menuListSearchRequestDto.getPage(),
            menuListSearchRequestDto.getSize(),
            menuListSearchRequestDto.getOrderby(),
            menuListSearchRequestDto.getSort()
        );
        Page<MenuListDto> menuListPage = menuQueryRepository.searchMenuListPage(
            pageRequest,
            storeId,
            menuListSearchRequestDto.getSearch()
        );

        return MenuMapper.toMenuListGetResponseDto(menuListPage);
    }

    public Menu getMenuById(String menuId) {
        return menuRepository.findById(menuId).orElseThrow(
            () -> new NotFoundException(
                messageUtil.getMessage("not-found.menu")));
    }
}
