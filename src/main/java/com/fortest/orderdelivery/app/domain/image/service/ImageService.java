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
import com.fortest.orderdelivery.app.domain.image.repository.ImageRepository;
import com.fortest.orderdelivery.app.global.exception.BusinessLogicException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ImageService {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final ImageRepository imageRepository;
    private final MessageSource messageSource;
    private final AmazonS3 amazonS3;

    // TODO : multipart upload로 업로드 중간에 실패할 때의 데이터 불일치 문제 해결하기
    @Transactional
    public MenuImageResponseDto updateImageToS3(List<MultipartFile> multipartFileList) {
        List<String> imageIdList = new ArrayList<>();

        if (!Objects.isNull(multipartFileList) && !multipartFileList.isEmpty()) {
            AtomicInteger sequence = new AtomicInteger(10);

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

                imageIdList.add(saveImage(sequence.get(), fileName));
                sequence.addAndGet(10);
            });
        }

        return ImageMapper.toMenuImageResponseDto(imageIdList);
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
    protected String saveImage(int sequence, String fileName) {
        Image image = Image.builder()
            .sequence(sequence)
            .fileName(fileName)
            .s3Url(amazonS3.getUrl(bucket, fileName).toString())
            .build();

        Image savedImage = imageRepository.save(image);
        return savedImage.getId();
    }

    public Image getImage(String id) {
        return imageRepository.findById(id).orElseThrow(
            () -> new BusinessLogicException(messageSource.getMessage(
                "image.get.failure", null, Locale.getDefault())));
    }

    private String getImageFileName(String id) {
        return getImage(id).getFileName();
    }

    // TODO : deleteBy 추가
    @Transactional
    protected String deleteImage(String id) {
        Image image = getImage(id);

        image.isDeletedNow(1L);
        Image savedImage = imageRepository.save(image);

        return savedImage.getId();
    }
}
