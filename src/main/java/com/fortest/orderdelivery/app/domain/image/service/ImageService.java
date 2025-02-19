package com.fortest.orderdelivery.app.domain.image.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.fortest.orderdelivery.app.domain.image.dto.MenuImageRequestDto;
import com.fortest.orderdelivery.app.domain.image.dto.MenuImageResponseDto;
import com.fortest.orderdelivery.app.domain.image.entity.Image;
import com.fortest.orderdelivery.app.domain.image.mapper.ImageMapper;
import com.fortest.orderdelivery.app.domain.image.repository.ImageQueryRepository;
import com.fortest.orderdelivery.app.domain.image.repository.ImageRepository;
import com.fortest.orderdelivery.app.domain.menu.dto.MenuAppResponseDto;
import com.fortest.orderdelivery.app.domain.menu.entity.Menu;
import com.fortest.orderdelivery.app.global.dto.CommonDto;
import com.fortest.orderdelivery.app.global.exception.BusinessLogicException;
import com.fortest.orderdelivery.app.global.exception.NotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersUriSpec;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

@Slf4j(topic = "ImageService")
@Service
@RequiredArgsConstructor
public class ImageService {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private static final String MENU_APP_URL = "http://{url}:{port}/api/app/menus";

    private final AmazonS3 amazonS3;
    private final WebClient webClient;
    private final MessageSource messageSource;
    private final ImageRepository imageRepository;
    private final ImageQueryRepository imageQueryRepository;

    // TODO : multipart upload로 업로드 중간에 실패할 때의 데이터 불일치 문제 해결하기
    @Transactional
    public MenuImageResponseDto registerMenuImage(List<MultipartFile> multipartFileList) {
        List<String> imageIdList = new ArrayList<>();

        if (!Objects.isNull(multipartFileList) && !multipartFileList.isEmpty()) {
            AtomicInteger sequence = new AtomicInteger(10);

            uploadImageToS3(multipartFileList, imageIdList, sequence, null);
        }

        return ImageMapper.toMenuImageResponseDto(imageIdList);
    }

    @Transactional
    public MenuImageResponseDto updateMenuImage(List<MultipartFile> multipartFileList,
        String menuId) {
        List<String> imageIdList = new ArrayList<>();

        if (!Objects.isNull(multipartFileList) && !multipartFileList.isEmpty()) {
            int maxImageSequence = imageQueryRepository.getMaxImageSequence(menuId);
            AtomicInteger sequence = new AtomicInteger(maxImageSequence + 10);

            CommonDto<MenuAppResponseDto> commonDto = getMenuFromApp(List.of(menuId));

            if (Objects.isNull(commonDto) || Objects.isNull(commonDto.getData())) {
                throw new NotFoundException(
                    messageSource.getMessage("not-found.menu", null,
                        Locale.getDefault()));
            }

            throwByRespCode(commonDto.getCode());

            uploadImageToS3(multipartFileList, imageIdList, sequence,
                commonDto.getData().getMenuList().get(0));
        }

        return ImageMapper.toMenuImageResponseDto(imageIdList);
    }

    @Transactional
    protected void uploadImageToS3(List<MultipartFile> multipartFileList, List<String> imageIdList,
        AtomicInteger sequence, Menu menu) {
        multipartFileList.forEach(file -> {

            String originalFileName = file.getOriginalFilename();
            validateFileExtension(originalFileName);
            String fileName = createFileName(originalFileName);

            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(file.getSize());
            objectMetadata.setContentType(file.getContentType());

            try (InputStream inputStream = file.getInputStream()) {
                amazonS3.putObject(
                    new PutObjectRequest(bucket, fileName, inputStream, objectMetadata)
                        .withCannedAcl(CannedAccessControlList.PublicRead));
            } catch (IOException | AmazonServiceException e) {
                throw new BusinessLogicException(messageSource.getMessage(
                    "s3.image.upload.failure", null, Locale.getDefault()));
            }

            imageIdList.add(saveImage(sequence.get(), fileName, menu));
            sequence.addAndGet(10);
        });
    }

    @Transactional
    public MenuImageResponseDto deleteImageFromS3(MenuImageRequestDto requestDto) {
        List<String> imageIdList = requestDto.getImageIdList();
        List<String> deleteImageIdList = new ArrayList<>();

        if (!Objects.isNull(imageIdList) && !imageIdList.isEmpty()) {
            List<DeleteObjectsRequest.KeyVersion> keysToDelete = imageIdList.stream()
                .map(id -> new DeleteObjectsRequest.KeyVersion(getImageFileName(id)))
                .collect(Collectors.toList());

            try {
                amazonS3.deleteObjects(new DeleteObjectsRequest(bucket)
                    .withKeys(keysToDelete)
                    .withQuiet(false));

                imageIdList.forEach(id -> deleteImageIdList.add(deleteImage(id)));

            } catch (AmazonServiceException e) {
                throw new BusinessLogicException(
                    messageSource.getMessage("s3.image.delete.failure", null, Locale.getDefault())
                );
            }
        }

        return ImageMapper.toMenuImageResponseDto(deleteImageIdList);
    }


    private String createFileName(String fileName) {
        return UUID.randomUUID().toString().substring(0, 15) + fileName;
    }

    private void validateFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf(".");

        if (lastDotIndex == -1) {
            throw new BusinessLogicException(messageSource.getMessage(
                "image.file.extension.error", null, Locale.getDefault()));
        }

        List<String> allowedExtentionList = Arrays.asList("jpg", "jpeg", "png", "gif");

        if (!allowedExtentionList.contains(fileName.substring(lastDotIndex + 1).toLowerCase())) {
            throw new BusinessLogicException(messageSource.getMessage(
                "image.file.extension.error", null, Locale.getDefault()));
        }
    }

    // TODO : createBy 추가
    @Transactional
    protected String saveImage(int sequence, String fileName, Menu menu) {
        Image image = Image.builder()
            .sequence(sequence)
            .fileName(fileName)
            .menu(menu)
            .s3Url(amazonS3.getUrl(bucket, fileName).toString())
            .build();

        Image savedImage = imageRepository.save(image);
        return savedImage.getId();
    }

    public Image getImageById(String id) {
        return imageRepository.findById(id).orElseThrow(
            () -> new BusinessLogicException(messageSource.getMessage(
                "image.get.failure", null, Locale.getDefault())));
    }

    private String getImageFileName(String id) {
        return getImageById(id).getFileName();
    }

    // TODO : deleteBy 추가
    @Transactional
    protected String deleteImage(String id) {
        Image image = getImageById(id);

        image.isDeletedNow(1L);
        Image savedImage = imageRepository.save(image);

        return savedImage.getId();
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
            .bodyToMono(new ParameterizedTypeReference<CommonDto<MenuAppResponseDto>>() {})
            .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(2))) // 에러 발생 시 2초 간격으로 최대 3회 재시도
            .onErrorResume(throwable -> {
                log.error("Fail : {}", finalUri, throwable);
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
