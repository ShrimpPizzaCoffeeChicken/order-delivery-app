package com.fortest.orderdelivery.app.domain.menu.service;

import com.fortest.orderdelivery.app.domain.menu.dto.MenuImageMappingRequestDto;
import com.fortest.orderdelivery.app.domain.menu.dto.MenuImageMappingResponseDto;
import com.fortest.orderdelivery.app.domain.menu.dto.MenuListGetResponseDto;
import com.fortest.orderdelivery.app.domain.menu.dto.MenuListGetResponseDto.MenuListDto;
import com.fortest.orderdelivery.app.domain.menu.dto.MenuSaveRequestDto;
import com.fortest.orderdelivery.app.domain.menu.dto.MenuSaveResponseDto;
import com.fortest.orderdelivery.app.domain.menu.entity.Menu;
import com.fortest.orderdelivery.app.domain.menu.mapper.MenuMapper;
import com.fortest.orderdelivery.app.domain.menu.repository.MenuRepository;
import com.fortest.orderdelivery.app.domain.menu.repository.MenuQueryRepository;
import com.fortest.orderdelivery.app.global.dto.CommonDto;
import com.fortest.orderdelivery.app.global.exception.BusinessLogicException;
import com.fortest.orderdelivery.app.global.util.JpaUtil;
import java.time.Duration;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
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
    private final MessageSource messageSource;
    private final MenuRepository menuRepository;
    private final MenuQueryRepository menuRepositoryQuery;

    private static final String STORE_APP_URL = "http://{url}:{port}/api/app/stores/{storeId}";
    private static final String IMAGE_APP_URL = "http://{url}:{port}/api/app/images/menus";

    // TODO : deleteBy 넣기
    public MenuSaveResponseDto saveMenu(MenuSaveRequestDto menuSaveRequestDto) {
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
                    messageSource.getMessage("image.menu.mapping.failure", null,
                        Locale.getDefault()));
            }

            throwByRespCode(commonDto.getCode());

            if (!commonDto.getData().getResult()) {
                throw new BusinessLogicException(
                    messageSource.getMessage("image.menu.mapping.failure", null,
                        Locale.getDefault()));
            }

        }

        return MenuMapper.toMenuSaveResponseDto(savedMenu);
    }

    public MenuListGetResponseDto getMenuList(String storeId, int page, int size, String orderBy,
        String sort) {
        PageRequest pageRequest = JpaUtil.getNormalPageable(page, size, orderBy, sort);
        Page<MenuListDto> menuListPage = menuRepositoryQuery.getMenuListPage(pageRequest, storeId);

        return MenuMapper.toMenuListGetResponseDto(menuListPage);
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

    /**
     * 이미지에 메뉴 Id update 요청
     *
     * @param requestDto
     * @return CommonDto<Void> : 요청 실패 시 null
     */
    public CommonDto<MenuImageMappingResponseDto> saveMenuIdToImage(
        MenuImageMappingRequestDto requestDto) {

        String targetUrl = IMAGE_APP_URL
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

    private void throwByRespCode(int httpStatusCode) {
        int firstNum = httpStatusCode / 100;
        switch (firstNum) {
            case 4 -> {
                throw new BusinessLogicException(
                    messageSource.getMessage("api.call.client-error", null, Locale.getDefault()));
            }
            case 5 -> {
                throw new BusinessLogicException(
                    messageSource.getMessage("api.call.server-error", null, Locale.getDefault()));
            }
        }
    }
}
