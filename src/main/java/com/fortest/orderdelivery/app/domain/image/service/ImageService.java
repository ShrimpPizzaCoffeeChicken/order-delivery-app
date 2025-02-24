package com.fortest.orderdelivery.app.domain.image.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.fortest.orderdelivery.app.domain.image.dto.ImageResponseDto;
import com.fortest.orderdelivery.app.domain.image.dto.MenuImageRequestDto;
import com.fortest.orderdelivery.app.domain.image.entity.Image;
import com.fortest.orderdelivery.app.domain.image.mapper.ImageMapper;
import com.fortest.orderdelivery.app.domain.image.repository.ImageQueryRepository;
import com.fortest.orderdelivery.app.domain.image.repository.ImageRepository;
import com.fortest.orderdelivery.app.domain.menu.dto.MenuAppResponseDto;
import com.fortest.orderdelivery.app.domain.menu.dto.MenuOptionAppResponseDto;
import com.fortest.orderdelivery.app.domain.menu.entity.Menu;
import com.fortest.orderdelivery.app.domain.menu.entity.MenuOption;
import com.fortest.orderdelivery.app.domain.menu.mapper.MenuMapper;
import com.fortest.orderdelivery.app.domain.user.entity.User;
import com.fortest.orderdelivery.app.global.exception.BusinessLogicException;
import com.fortest.orderdelivery.app.global.gateway.ApiGateway;
import com.fortest.orderdelivery.app.global.util.MessageUtil;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j(topic = "ImageService")
@Service
@RequiredArgsConstructor
public class ImageService {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private static final String MENU_OPTION_APP_URL = "http://{url}:{port}/api/app/menus/options";

    private final ApiGateway apiGateway;
    private final AmazonS3 amazonS3;
    private final MessageUtil messageUtil;
    private final ImageRepository imageRepository;
    private final ImageQueryRepository imageQueryRepository;

    // TODO : multipart upload로 업로드 중간에 실패할 때의 데이터 불일치 문제 해결하기
    @Transactional
    public ImageResponseDto registerMenuImage(List<MultipartFile> multipartFileList, User user) {
        List<String> imageIdList = new ArrayList<>();

        if (!Objects.isNull(multipartFileList) && !multipartFileList.isEmpty()) {
            AtomicInteger sequence = new AtomicInteger(10);

            uploadImageToS3(multipartFileList, imageIdList, sequence, null, null, user);
        }

        return ImageMapper.toImageResponseDto(imageIdList);
    }

    @Transactional
    public ImageResponseDto updateMenuImage(List<MultipartFile> multipartFileList, String menuId,
        User user) {
        List<String> imageIdList = new ArrayList<>();

        if (!Objects.isNull(multipartFileList) && !multipartFileList.isEmpty()) {
            int maxImageSequence = imageQueryRepository.getMaxMenuImageSequence(menuId);
            AtomicInteger sequence = new AtomicInteger(maxImageSequence + 10);

            MenuAppResponseDto menuDto = apiGateway.getMenuFromApp(List.of(menuId), user);

            uploadImageToS3(multipartFileList, imageIdList, sequence,
                MenuMapper.toMenu(menuDto.getMenuList().get(0)), null, user);
        }

        return ImageMapper.toImageResponseDto(imageIdList);
    }

    @Transactional
    public ImageResponseDto updateMenuOptionImage(List<MultipartFile> multipartFileList,
        String menuOptionId, User user) {
        List<String> imageIdList = new ArrayList<>();

        if (!Objects.isNull(multipartFileList) && !multipartFileList.isEmpty()) {
            int maxImageSequence = imageQueryRepository.getMaxMenuOptionImageSequence(menuOptionId);
            AtomicInteger sequence = new AtomicInteger(maxImageSequence + 10);

            MenuOptionAppResponseDto menuOptionDto = apiGateway.getMenuOptionFromApp(
                List.of(menuOptionId), user);

            MenuOption menuOption = menuOptionDto.getMenuOptionList().get(0);

            uploadImageToS3(multipartFileList, imageIdList, sequence,
                menuOption.getMenu(), menuOption, user);
        }

        return ImageMapper.toImageResponseDto(imageIdList);
    }

    @Transactional
    protected void uploadImageToS3(List<MultipartFile> multipartFileList, List<String> imageIdList,
        AtomicInteger sequence, Menu menu, MenuOption menuOption, User user) {
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
                throw new BusinessLogicException(messageUtil.getMessage("s3.image.upload.failure"));
            }

            imageIdList.add(saveImage(sequence.get(), fileName, menu, menuOption, user));
            sequence.addAndGet(10);
        });
    }

    @Transactional
    public ImageResponseDto deleteImageOnUpdate(MenuImageRequestDto requestDto, User user) {
        List<String> imageIdList = requestDto.getImageIdList();
        List<String> deleteImageIdList = deleteImageFromS3(imageIdList, user);

        return ImageMapper.toImageResponseDto(deleteImageIdList);
    }

    @Transactional
    public List<String> deleteImageFromS3(List<String> imageIdList, User user) {
        List<String> deleteImageIdList = new ArrayList<>();

        if (!Objects.isNull(imageIdList) && !imageIdList.isEmpty()) {
            List<DeleteObjectsRequest.KeyVersion> keysToDelete = imageIdList.stream()
                .map(id -> new DeleteObjectsRequest.KeyVersion(getImageFileName(id)))
                .collect(Collectors.toList());

            try {
                amazonS3.deleteObjects(new DeleteObjectsRequest(bucket)
                    .withKeys(keysToDelete)
                    .withQuiet(false));

                deleteImageIdList = imageQueryRepository.deleteImagesAndReturn(imageIdList,
                        user.getId()).stream()
                    .map(Image::getId)
                    .toList();

            } catch (AmazonServiceException e) {
                throw new BusinessLogicException(
                    messageUtil.getMessage("s3.image.delete.failure"));
            }
        }
        return deleteImageIdList;
    }


    private String createFileName(String fileName) {
        return UUID.randomUUID().toString().substring(0, 15) + fileName;
    }

    private void validateFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf(".");

        if (lastDotIndex == -1) {
            throw new BusinessLogicException(messageUtil.getMessage(
                "image.file.extension.error"));
        }

        List<String> allowedExtentionList = Arrays.asList("jpg", "jpeg", "png", "gif");

        if (!allowedExtentionList.contains(fileName.substring(lastDotIndex + 1).toLowerCase())) {
            throw new BusinessLogicException(messageUtil.getMessage(
                "image.file.extension.error"));
        }
    }

    @Transactional
    protected String saveImage(int sequence, String fileName, Menu menu, MenuOption menuOption,
        User user) {
        Image image = Image.builder()
            .sequence(sequence)
            .fileName(fileName)
            .menu(menu)
            .menuOption(menuOption)
            .s3Url(amazonS3.getUrl(bucket, fileName).toString())
            .build();

        image.isCreatedBy(user.getId());
        Image savedImage = imageRepository.save(image);
        return savedImage.getId();
    }

    public Image getImageById(String id) {
        return imageRepository.findById(id).orElseThrow(
            () -> new BusinessLogicException(messageUtil.getMessage("image.get.failure")));
    }

    private String getImageFileName(String id) {
        return getImageById(id).getFileName();
    }

    @Transactional
    protected String deleteImage(String id, User user) {
        Image image = getImageById(id);

        image.isDeletedNow(user.getId());
        Image savedImage = imageRepository.save(image);

        return savedImage.getId();
    }
}
