package com.fortest.orderdelivery.app.domain.store.service;

import com.fortest.orderdelivery.app.domain.store.dto.*;
import com.fortest.orderdelivery.app.domain.store.entity.Store;
import com.fortest.orderdelivery.app.domain.store.mapper.StoreMapper;
import com.fortest.orderdelivery.app.domain.store.repository.StoreRepository;
import com.fortest.orderdelivery.app.global.dto.CommonDto;
import com.fortest.orderdelivery.app.global.exception.BusinessLogicException;
import com.fortest.orderdelivery.app.global.exception.NotFoundException;
import com.fortest.orderdelivery.app.global.util.MessageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;

@Slf4j
@Service
@RequiredArgsConstructor
public class StoreAppService {

    private final WebClient webClient;
    private final MessageUtil messageUtil;
    private final StoreRepository storeRepository;

    private static final String CONTENT_TYPE = "Content-Type";
    private static final String MENU_APP_URL = "http://{host}:{port}/api/app/menus/options/valid";

    public StoreMappingResponseDto getStoreId(String storeId) {
        return StoreMappingResponseDto.builder()
                .storeId(storeId)
                .build();
    }

    public StoreMenuValidResponseDto getStoreMenuValid(String storeId, StoreMenuValidRequestDto requestDto) {
        // store
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new NotFoundException(messageUtil.getMessage("not-found.store")));

        // menu 검증 요청
        MenuOptionValidRequestDto menuOptionValidRequestDto = StoreMapper.storeValidReqDtoToMenuValidRedDto(requestDto);
        CommonDto<MenuOptionValidReponseDto> validMenuOptionFromApp = getValidMenuOptionFromApp(store.getId(), menuOptionValidRequestDto);
        if (validMenuOptionFromApp == null || validMenuOptionFromApp.getData() == null) {
            throw new BusinessLogicException(messageUtil.getMessage("api.call.server-error"));
        }

        return StoreMapper.menuOptionResDtoToStoreValidResDto(store, validMenuOptionFromApp.getData());
    }

    // 메뉴 검증 요청
    // TODO : 하단 코드로 교체 예정
    private CommonDto<MenuOptionValidReponseDto> getValidMenuOptionFromApp(String storeId, MenuOptionValidRequestDto validReqDto) {
        ArrayList<MenuOptionValidReponseDto.MenuDto> menuList = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            ArrayList<MenuOptionValidReponseDto.OptionDto> optionList = new ArrayList<>();
            for (int j = 0; j < 2; j++) {
                optionList.add(
                        MenuOptionValidReponseDto.OptionDto.builder()
                                .name("optionName" + String.valueOf(i) + String.valueOf(j))
                                .id("option" + String.valueOf(i) + String.valueOf(j))
                                .price(1 + j * 1000)
                                .build()
                );
            }
            menuList.add(
                    MenuOptionValidReponseDto.MenuDto.builder()
                            .name("menuName" + String.valueOf(i))
                            .id("menu" + i)
                            .optionList(optionList)
                            .price(1 + i * 1000)
                            .build()
            );
        }

        MenuOptionValidReponseDto validDto = MenuOptionValidReponseDto.builder()
                .result(true)
                .menuList(menuList)
                .build();

        return new CommonDto<>("SUCCESS", HttpStatus.OK.value(), validDto);
    }

//    /**
//     * 가게, 메뉴, 옵션 유효성 검사 요청
//     * @param
//     * @param
//     * @return CommonDto<StoreMenuValidResDto> : 요청 실패 시 null
//     */
//    public CommonDto<MenuOptionValidReponseDto> getValidMenuOptionFromApp(String storeId, MenuOptionValidRequestDto validReqDto) {
//
//        String targetUrl = MENU_APP_URL
//                .replace("{host}", "localhost")
//                .replace("{port}", "8082");
//
//        // 요청 파라미터 생성
//        JSONObject paramObject = new JSONObject(validReqDto);
//        Map<String, String> queryParam = Map.of(
//                "data", paramObject.toString()
//        );
//
//        return webClient.get()
//                .uri(targetUrl, queryParam, storeId)
//                .header(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
//                .retrieve()
//                .bodyToMono(new ParameterizedTypeReference<CommonDto<MenuOptionValidReponseDto>>() {})
//                .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(2))) //에러 발생 시 2초 간격으로 최대 3회 재시도
//                .onErrorResume(throwable -> {
//                    log.error("Fail : {}, {}", targetUrl, queryParam, throwable);
//                    return Mono.empty();
//                })
//                .block();
//    }
}