package com.fortest.orderdelivery.app.domain.menu.service;

import com.fortest.orderdelivery.app.domain.image.dto.ImageResponseDto;
import com.fortest.orderdelivery.app.domain.menu.dto.MenuImageMappingRequestDto;
import com.fortest.orderdelivery.app.domain.menu.dto.MenuImageMappingResponseDto;
import com.fortest.orderdelivery.app.domain.menu.dto.MenuListGetResponseDto;
import com.fortest.orderdelivery.app.domain.menu.dto.MenuListGetResponseDto.MenuListDto;
import com.fortest.orderdelivery.app.domain.menu.dto.MenuResponseDto;
import com.fortest.orderdelivery.app.domain.menu.dto.MenuSaveRequestDto;
import com.fortest.orderdelivery.app.domain.menu.dto.MenuUpdateRequestDto;
import com.fortest.orderdelivery.app.domain.menu.entity.ExposeStatus;
import com.fortest.orderdelivery.app.domain.menu.entity.Menu;
import com.fortest.orderdelivery.app.domain.menu.mapper.MenuMapper;
import com.fortest.orderdelivery.app.domain.menu.repository.MenuQueryRepository;
import com.fortest.orderdelivery.app.domain.menu.repository.MenuRepository;
import com.fortest.orderdelivery.app.global.dto.CommonDto;
import com.fortest.orderdelivery.app.global.exception.BusinessLogicException;
import com.fortest.orderdelivery.app.global.exception.NotFoundException;
import com.fortest.orderdelivery.app.global.util.JpaUtil;
import com.fortest.orderdelivery.app.global.util.MessageUtil;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

@Slf4j(topic = "MenuService")
@Service
@RequiredArgsConstructor
public class MenuService {

    private final WebClient webClient;
    private final MessageUtil messageUtil;
    private final MenuRepository menuRepository;
    private final MenuQueryRepository menuRepositoryQuery;

    private static final String STORE_APP_URL = "http://{url}:{port}/api/app/stores/{storeId}";
    private static final String IMAGE_UPDATE_APP_URL = "http://{url}:{port}/api/app/images/menus";
    private static final String IMAGE_DELETE_APP_URL = "http://{url}:{port}/api/app/images/menus/{menuId}";

    // TODO : deleteBy 넣기
    public MenuResponseDto saveMenu(MenuSaveRequestDto menuSaveRequestDto) {
        String storeId = menuSaveRequestDto.getStoreId();

//        가게 DB 존재 여부 확인 API 생성 후 테스트
//        CommonDto<StoreValidResponseDto> commonDto = getValidStoreFromApp(storeId);
//        if(Objects.isNull(commonDto) || Objects.isNull(commonDto.getData())) {
//            throw new BusinessLogicException("Store is not Valid");
//        }

        Menu newMenu = MenuMapper.toMenu(menuSaveRequestDto);
        Menu savedMenu = menuRepository.save(newMenu);

        List<String> imageIdList = menuSaveRequestDto.getImageIdList();

        if (!Objects.isNull(imageIdList)) {
            MenuImageMappingRequestDto menuImageRequestDto = MenuImageMappingRequestDto.builder()
                .imageIdList(imageIdList)
                .menu(savedMenu)
                .build();

            CommonDto<MenuImageMappingResponseDto> commonDto = saveMenuIdToImage(
                menuImageRequestDto);

            if (Objects.isNull(commonDto) || Objects.isNull(commonDto.getData())) {
                throw new BusinessLogicException(
                    messageUtil.getMessage("image.menu.mapping.failure"));
            }

            throwByRespCode(commonDto.getCode());

            if (!commonDto.getData().getResult()) {
                throw new BusinessLogicException(
                    messageUtil.getMessage("image.menu.mapping.failure"));
            }

        }

        return MenuMapper.toMenuResponseDto(savedMenu);
    }

    public MenuListGetResponseDto getMenuList(String storeId, int page, int size, String orderBy,
        String sort) {
        PageRequest pageRequest = JpaUtil.getNormalPageable(page, size, orderBy, sort);
        Page<MenuListDto> menuListPage = menuRepositoryQuery.getMenuListPage(pageRequest, storeId);

        return MenuMapper.toMenuListGetResponseDto(menuListPage);
    }

    // TODO : updatedBy 변경
    @Transactional
    public MenuResponseDto updateMenu(MenuUpdateRequestDto menuUpdateRequestDto,
        String menuId) {
        Menu menu = getMenuById(menuId);

        menu.updateMenu(
            menuUpdateRequestDto.getName(),
            menuUpdateRequestDto.getDescription(),
            menuUpdateRequestDto.getPrice(),
            ExposeStatus.valueOf(menuUpdateRequestDto.getExposeStatus()));
        menu.isUpdatedNow(1L);

        Menu savedMenu = menuRepository.save(menu);

        return MenuMapper.toMenuResponseDto(savedMenu);
    }

    // TODO : deleteBy 변경
    @Transactional
    public MenuResponseDto deleteMenu(String menuId) {
        CommonDto<ImageResponseDto> commonDto = deleteMenuImageFromApp(menuId);

        if (Objects.isNull(commonDto) || Objects.isNull(commonDto.getData())) {
            throw new BusinessLogicException(
                messageUtil.getMessage("s3.image.delete.failure"));
        }

        throwByRespCode(commonDto.getCode());

        Menu menu = getMenuById(menuId);
        menu.isDeletedNow(1L);
        Menu savedMenu = menuRepository.save(menu);

        return MenuMapper.toMenuResponseDto(savedMenu);

    }

    /**
     * 이미지 서비스에 메뉴 옵션 Id로 메뉴 옵션 이미지 삭제 요청
     *
     * @param menuId
     * @return CommonDto<ImageResponseDto> : 요청 실패 시 null
     */
    public CommonDto<ImageResponseDto> deleteMenuImageFromApp(String menuId) {

        String targetUrl = IMAGE_DELETE_APP_URL
            .replace("{url}", "localhost")
            .replace("{port}", "8082")
            .replace("{menuId}", menuId);

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
     * 가게유효성 검사 요청
     *
     * @param storeId
     * @return CommonDto<StoreValidResponseDto> : 요청 실패 시 null
     */
//    public CommonDto<StoreValidResponseDto> getValidStoreFromApp(String storeId) {
//
//        String targetUrl = STORE_APP_URL
//            .replace("{url}", "localhost")
//            .replace("{port}", "8082")
//            .replace("{storeId}", storeId);
//
//        return webClient.get()
//            .uri(targetUrl)
//            .retrieve()
//            .bodyToMono(new ParameterizedTypeReference<CommonDto<StoreValidResponseDto>>() {
//            })
//            .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(2))) //에러 발생 시 2초 간격으로 최대 3회 재시도
//            .onErrorResume(throwable -> {
//                log.error("Fail : {}", targetUrl, throwable);
//                return Mono.empty();
//            })
//            .block();
//    }

    public MenuListGetResponseDto searchMenuList(String storeId, int page, int size, String orderBy,
        String sort, String keyword) {
        PageRequest pageRequest = JpaUtil.getNormalPageable(page, size, orderBy, sort);
        Page<MenuListDto> menuListPage = menuRepositoryQuery.searchMenuListPage(pageRequest, storeId, keyword);

        return MenuMapper.toMenuListGetResponseDto(menuListPage);
    }

    /**
     * 이미지에 메뉴 Id update 요청
     *
     * @param requestDto
     * @return CommonDto<Void> : 요청 실패 시 null
     */
    public CommonDto<MenuImageMappingResponseDto> saveMenuIdToImage(
        MenuImageMappingRequestDto requestDto) {

        String targetUrl = IMAGE_UPDATE_APP_URL
            .replace("{url}", "localhost")
            .replace("{port}", "8082");

        return webClient.patch()
            .uri(targetUrl)
            .body(Mono.justOrEmpty(requestDto), MenuImageMappingRequestDto.class)
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<CommonDto<MenuImageMappingResponseDto>>() {
            })
            .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(2))) //에러 발생 시 2초 간격으로 최대 3회 재시도
            .onErrorResume(throwable -> {
                log.error("Fail : {}", targetUrl, throwable);
                return Mono.empty();
            })
            .block();
    }

    public Menu getMenuById(String menuId) {
        return menuRepository.findById(menuId).orElseThrow(
            () -> new NotFoundException(
                messageUtil.getMessage("not-found.menu")));
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
}
