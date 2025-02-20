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
import com.fortest.orderdelivery.app.global.dto.CommonDto;
import com.fortest.orderdelivery.app.global.exception.BusinessLogicException;
import com.fortest.orderdelivery.app.global.exception.NotFoundException;
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

    private final WebClient webClient;
    private final MessageUtil messageUtil;
    private final MenuOptionRepository menuOptionRepository;

    // TODO : createdBy 추가
    public MenuOptionResponseDto saveMenuOption(
        MenuOptionsSaveRequestDto menuOptionsSaveRequestDto, String menuId) {

        //메뉴 유효한지 확인 후 객체 가져오기
        CommonDto<MenuAppResponseDto> menuCommonDto = getMenuFromApp(List.of(menuId));

        if (Objects.isNull(menuCommonDto) || Objects.isNull(menuCommonDto.getData())) {
            throw new NotFoundException(
                messageUtil.getMessage("not-found.menu"));
        }
        throwByRespCode(menuCommonDto.getCode());

        Menu menu = menuCommonDto.getData().getMenuList().get(0);

        MenuOption newMenuOption = MenuOptionMapper.toMenuOption(menuOptionsSaveRequestDto, menu);
        MenuOption savedMenuOption = menuOptionRepository.save(newMenuOption);

        //만약 image Url이 있다면 imageUrl로 정보 보내기 (메뉴 + 메뉴 옵션)
        List<String> imageIdList = menuOptionsSaveRequestDto.getImageIdList();

        if (!Objects.isNull(imageIdList)) {
            MenuOptionImageMappingRequestDto menuOptionImageRequestDto = MenuOptionImageMappingRequestDto.builder()
                .imageIdList(imageIdList)
                .menu(menu)
                .menuOption(savedMenuOption)
                .build();

            CommonDto<MenuOptionImageMappingResponseDto> ImagecommonDto = saveMenuAndMenuOptionIdToImage(
                menuOptionImageRequestDto);

            if (Objects.isNull(ImagecommonDto) || Objects.isNull(ImagecommonDto.getData())) {
                throw new BusinessLogicException(
                    messageUtil.getMessage("image.menu.mapping.failure"));
            }

            throwByRespCode(ImagecommonDto.getCode());

            if (!ImagecommonDto.getData().getResult()) {
                throw new BusinessLogicException(
                    messageUtil.getMessage("image.menu.mapping.failure"));
            }

        }

        return MenuOptionMapper.toMenuOptionSaveResponseDto(savedMenuOption);
    }

    // TODO : updatedBy 변경
    @Transactional
    public MenuOptionResponseDto updateMenuOption(
        MenuOptionUpdateRequestDto menuOptionUpdateRequestDto,
        String menuOptionId) {
        MenuOption menuOption = getMenuOptionById(menuOptionId);

        menuOption.updateMenuOption(
            menuOptionUpdateRequestDto.getName(),
            menuOptionUpdateRequestDto.getDescription(),
            menuOptionUpdateRequestDto.getPrice(),
            ExposeStatus.valueOf(menuOptionUpdateRequestDto.getExposeStatus()));

        menuOption.isUpdatedNow(1L);
        MenuOption savedMenuOption = menuOptionRepository.save(menuOption);

        return MenuMapper.toMenuOptionResponseDto(savedMenuOption);
    }

    // TODO : deleteBy 변경
    @Transactional
    public MenuOptionResponseDto deleteMenuOption(String optionId) {
        CommonDto<ImageResponseDto> commonDto = deleteMenuOptionImageFromApp(optionId);

        if (Objects.isNull(commonDto) || Objects.isNull(commonDto.getData())) {
            throw new BusinessLogicException(
                messageUtil.getMessage("s3.image.delete.failure"));
        }

        throwByRespCode(commonDto.getCode());

        MenuOption menuOption = getMenuOptionById(optionId);
        menuOption.isDeletedNow(1L);
        MenuOption savedMenuOption = menuOptionRepository.save(menuOption);

        return MenuMapper.toMenuOptionResponseDto(savedMenuOption);
    }

    /**
     * 이미지 서비스에 메뉴 옵션 Id로 메뉴 옵션 이미지 삭제 요청
     *
     * @param optionId
     * @return CommonDto<ImageResponseDto> : 요청 실패 시 null
     */
    public CommonDto<ImageResponseDto> deleteMenuOptionImageFromApp(String optionId) {

        String targetUrl = IMAGE_DELETE_APP_URL
            .replace("{url}", "localhost")
            .replace("{port}", "8082")
            .replace("{optionId}", optionId);

        return webClient.delete()
            .uri(targetUrl)
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<CommonDto<ImageResponseDto>>() {
            })
            .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(2))) // 에러 발생 시 2초 간격으로 최대 3회 재시도
            .onErrorResume(throwable -> {
                log.error("Fail : {}", targetUrl, throwable);
                return Mono.empty();
            })
            .block();
    }

    /**
     * 메뉴 서비스에 메뉴 Id로 메뉴 객체 요청
     *
     * @param menuIdList
     * @return CommonDto<MenuAppResponseDto> : 요청 실패 시 null
     */
    public CommonDto<MenuAppResponseDto> getMenuFromApp(List<String> menuIdList) {

        String targetUrl = MENU_APP_URL
            .replace("{url}", "localhost")
            .replace("{port}", "8082");

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(targetUrl)
            .queryParam("menuId", menuIdList);

        String finalUri = uriBuilder.build().toString();

        return webClient.get()
            .uri(finalUri)
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<CommonDto<MenuAppResponseDto>>() {
            })
            .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(2))) // 에러 발생 시 2초 간격으로 최대 3회 재시도
            .onErrorResume(throwable -> {
                log.error("Fail : {}", finalUri, throwable);
                return Mono.empty();
            })
            .block();
    }

    /**
     * 이미지에 메뉴 옵션 Id update 요청
     *
     * @param requestDto
     * @return CommonDto<Void> : 요청 실패 시 null
     */
    public CommonDto<MenuOptionImageMappingResponseDto> saveMenuAndMenuOptionIdToImage(
        MenuOptionImageMappingRequestDto requestDto) {

        String targetUrl = IMAGE_UPDATE_APP_URL
            .replace("{url}", "localhost")
            .replace("{port}", "8082");

        return webClient.patch()
            .uri(targetUrl)
            .body(Mono.justOrEmpty(requestDto), MenuImageMappingRequestDto.class)
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .retrieve()
            .bodyToMono(
                new ParameterizedTypeReference<CommonDto<MenuOptionImageMappingResponseDto>>() {
                })
            .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(2))) //에러 발생 시 2초 간격으로 최대 3회 재시도
            .onErrorResume(throwable -> {
                log.error("Fail : {}", targetUrl, throwable);
                return Mono.empty();
            })
            .block();
    }

    private void throwByRespCode(int httpStatusCode) {
        int firstNum = httpStatusCode / 100;
        switch (firstNum) {
            case 4 -> {
                throw new BusinessLogicException(
                    messageUtil.getMessage("api.call.client-error"));
            }
            case 5 -> {
                throw new BusinessLogicException(
                    messageUtil.getMessage("api.call.server-error"));
            }
        }
    }

    public MenuOption getMenuOptionById(String menuOptionId) {
        return menuOptionRepository.findById(menuOptionId).orElseThrow(
            () -> new NotFoundException(
                messageUtil.getMessage("not-found.menu.option")));
    }
}
