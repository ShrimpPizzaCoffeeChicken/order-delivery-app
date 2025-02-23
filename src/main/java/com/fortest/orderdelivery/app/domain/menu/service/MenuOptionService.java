package com.fortest.orderdelivery.app.domain.menu.service;

import com.fortest.orderdelivery.app.domain.image.dto.ImageResponseDto;
import com.fortest.orderdelivery.app.domain.menu.dto.MenuAppResponseDto;
import com.fortest.orderdelivery.app.domain.menu.dto.MenuImageMappingRequestDto;
import com.fortest.orderdelivery.app.domain.menu.dto.MenuOptionImageMappingRequestDto;
import com.fortest.orderdelivery.app.domain.menu.dto.MenuOptionImageMappingResponseDto;
import com.fortest.orderdelivery.app.domain.menu.dto.MenuOptionResponseDto;
import com.fortest.orderdelivery.app.domain.menu.dto.MenuOptionUpdateRequestDto;
import com.fortest.orderdelivery.app.domain.menu.dto.MenuOptionsSaveRequestDto;
import com.fortest.orderdelivery.app.domain.menu.entity.ExposeStatus;
import com.fortest.orderdelivery.app.domain.menu.entity.Menu;
import com.fortest.orderdelivery.app.domain.menu.entity.MenuOption;
import com.fortest.orderdelivery.app.domain.menu.mapper.MenuMapper;
import com.fortest.orderdelivery.app.domain.menu.mapper.MenuOptionMapper;
import com.fortest.orderdelivery.app.domain.menu.repository.MenuOptionRepository;
import com.fortest.orderdelivery.app.domain.user.entity.User;
import com.fortest.orderdelivery.app.global.dto.CommonDto;
import com.fortest.orderdelivery.app.global.exception.BusinessLogicException;
import com.fortest.orderdelivery.app.global.exception.NotFoundException;
import com.fortest.orderdelivery.app.global.gateway.ApiGateway;
import com.fortest.orderdelivery.app.global.util.MessageUtil;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

@Slf4j(topic = "MenuOptionService")
@Service
@RequiredArgsConstructor
public class MenuOptionService {

    private static final String MENU_APP_URL = "http://{url}:{port}/api/app/menus";
    private static final String IMAGE_UPDATE_APP_URL = "http://{url}:{port}/api/app/images/options";
    private static final String IMAGE_DELETE_APP_URL = "http://{url}:{port}/api/app/images/options/{optionId}";

    private final ApiGateway apiGateway;
    private final MessageUtil messageUtil;
    private final MenuOptionRepository menuOptionRepository;

    public MenuOptionResponseDto saveMenuOption(
        MenuOptionsSaveRequestDto menuOptionsSaveRequestDto, String menuId, User user) {
        Long userId = user.getId();

        //메뉴 유효한지 확인 후 객체 가져오기
        MenuAppResponseDto menuDto = apiGateway.getMenuFromApp(List.of(menuId));

        Menu menu = menuDto.getMenuList().get(0);

        MenuOption newMenuOption = MenuOptionMapper.toMenuOption(menuOptionsSaveRequestDto, menu);
        newMenuOption.isCreatedBy(userId);
        MenuOption savedMenuOption = menuOptionRepository.save(newMenuOption);

        //만약 image Url이 있다면 imageUrl로 정보 보내기 (메뉴 + 메뉴 옵션)
        List<String> imageIdList = menuOptionsSaveRequestDto.getImageIdList();

        if (!Objects.isNull(imageIdList)) {
            MenuOptionImageMappingRequestDto menuOptionImageRequestDto = MenuOptionImageMappingRequestDto.builder()
                .imageIdList(imageIdList)
                .menu(menu)
                .menuOption(savedMenuOption)
                .build();

            MenuOptionImageMappingResponseDto ImagecommonDto =
                apiGateway.saveMenuAndMenuOptionIdToImage(menuOptionImageRequestDto, user);

            if (!ImagecommonDto.getResult()) {
                throw new BusinessLogicException(
                    messageUtil.getMessage("image.menu.mapping.failure"));
            }
        }

        return MenuOptionMapper.toMenuOptionSaveResponseDto(savedMenuOption);
    }

    @Transactional
    public MenuOptionResponseDto updateMenuOption(
        MenuOptionUpdateRequestDto menuOptionUpdateRequestDto,
        String menuOptionId,
        User user) {
        MenuOption menuOption = getMenuOptionById(menuOptionId);

        menuOption.updateMenuOption(
            menuOptionUpdateRequestDto.getName(),
            menuOptionUpdateRequestDto.getDescription(),
            menuOptionUpdateRequestDto.getPrice(),
            ExposeStatus.valueOf(menuOptionUpdateRequestDto.getExposeStatus()));

        menuOption.isUpdatedNow(user.getId());
        MenuOption savedMenuOption = menuOptionRepository.save(menuOption);

        return MenuMapper.toMenuOptionResponseDto(savedMenuOption);
    }

    @Transactional
    public MenuOptionResponseDto deleteMenuOption(String optionId, User user) {
        ImageResponseDto commonDto = apiGateway.deleteMenuOptionImageFromApp(optionId, user);

        MenuOption menuOption = getMenuOptionById(optionId);
        menuOption.isDeletedNow(user.getId());
        MenuOption savedMenuOption = menuOptionRepository.save(menuOption);

        return MenuMapper.toMenuOptionResponseDto(savedMenuOption);
    }

    public MenuOption getMenuOptionById(String menuOptionId) {
        return menuOptionRepository.findById(menuOptionId).orElseThrow(
            () -> new NotFoundException(
                messageUtil.getMessage("not-found.menu.option")));
    }
}
