package com.fortest.orderdelivery.app.domain.image.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.fortest.orderdelivery.app.domain.image.dto.MenuImageSaveResponseDto;
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

    @Transactional
    public MenuImageSaveResponseDto updateImageToS3(List<MultipartFile> multipartFileList) {
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
                } catch (IOException e) {
                    throw new BusinessLogicException(messageSource.getMessage(
                        "s3.image.upload.failure", null, Locale.getDefault()));
                }

                imageIdList.add(saveImage(sequence.get(), fileName));
                sequence.addAndGet(10);
            });
        }

        return ImageMapper.toMenuImageSaveResponseDto(imageIdList);
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

    private String saveImage(int sequence, String fileName) {
        Image image = Image.builder()
            .sequence(sequence)
            .s3Url(amazonS3.getUrl(bucket, fileName).toString())
            .build();

        Image savedImage = imageRepository.save(image);
        return savedImage.getId();
    }
}
