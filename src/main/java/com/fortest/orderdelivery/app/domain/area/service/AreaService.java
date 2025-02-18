package com.fortest.orderdelivery.app.domain.area.service;

import com.fortest.orderdelivery.app.domain.area.dto.AreaSaveRequestDto;
import com.fortest.orderdelivery.app.domain.area.dto.UserResponseDto;
import com.fortest.orderdelivery.app.domain.area.entity.Area;
import com.fortest.orderdelivery.app.domain.area.mapper.AreaMapper;
import com.fortest.orderdelivery.app.domain.area.repository.AreaRepository;
import com.fortest.orderdelivery.app.global.dto.CommonDto;
import com.fortest.orderdelivery.app.global.exception.BusinessLogicException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Locale;

@Slf4j
@RequiredArgsConstructor
@Service
public class AreaService {

    private final WebClient webClient;
    private final MessageSource messageSource;
    private final AreaRepository areaRepository;

    /**
     * 지역 생성
     * @param saveDto
     * @return 생성된 Area Entity
     */
    @Transactional
    public Area saveArea(AreaSaveRequestDto saveDto, Long userId) {

        // 회원 정보 확인
        CommonDto<UserResponseDto> validUserResponse = getValidUserFromApp(userId); // api 요청
        throwByRespCode(validUserResponse.getCode());
        if (validUserResponse == null || validUserResponse.getData() == null) {
            throw new BusinessLogicException(messageSource.getMessage("api.call.server-error", null, Locale.KOREA));
        }
        Long validUserId = validUserResponse.getData().getUserId();

        Area area = AreaMapper.saveDtoToEntity(saveDto);
        area.isCreatedBy(validUserId);
        return areaRepository.save(area);
    }

    @Transactional
    public String deleteArea(String areaId, Long userId) {
        // 회원 정보 확인
        CommonDto<UserResponseDto> validUserResponse = getValidUserFromApp(userId); // api 요청
        throwByRespCode(validUserResponse.getCode());
        if (validUserResponse == null || validUserResponse.getData() == null) {
            throw new BusinessLogicException(messageSource.getMessage("api.call.server-error", null, Locale.KOREA));
        }
        Long validUserId = validUserResponse.getData().getUserId();

        Area area = areaRepository.findById(areaId)
                .orElseThrow(() -> new BusinessLogicException(messageSource.getMessage("api.call.client-error", null, Locale.KOREA)));

        area.isDeletedNow(validUserId);
        return area.getId();
    }

    // TODO : 하단 코드로 교체 예정
    private CommonDto<UserResponseDto> getValidUserFromApp(Long userId) {
        String userName = "testUser";

        UserResponseDto userDto = UserResponseDto.builder()
                .userId(userId)
                .username(userName)
                .build();

        return new CommonDto<>("SUCCESS", HttpStatus.OK.value(), userDto);
    }

    // private CommonDto<UserResponseDto> getValidUserFromApp(Long userId) {
    //     String targetUrl = USER_APP_URL
    //             .replace("{host}", "localhost")
    //             .replace("{port}", "8082")
    //             .replace("{userId}", userId);
    //     return webClient.get()
    //             .uri(targetUrl)
    //             .header(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
    //             .retrieve()
    //             .bodyToMono(new ParameterizedTypeReference<CommonDto<UserResponseDto>>() {})
    //             .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(2))) //에러 발생 시 2초 간격으로 최대 3회 재시도
    //             .onErrorResume(throwable -> {
    //                 log.error("Fail : {}", targetUrl, throwable);
    //                 return Mono.empty();
    //             })
    //             .block();
    // }

    private void throwByRespCode(int httpStatusCode) {
        int firstNum = httpStatusCode / 100;
        switch (firstNum) {
            case 4 -> {
                throw new BusinessLogicException(messageSource.getMessage("api.call.client-error", null, Locale.KOREA));
            }
            case 5 -> {
                throw new BusinessLogicException(messageSource.getMessage("api.call.server-error", null, Locale.KOREA));
            }
        }
    }
}
