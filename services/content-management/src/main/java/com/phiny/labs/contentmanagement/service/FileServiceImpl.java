package com.phiny.labs.contentmanagement.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.phiny.labs.contentmanagement.config.EntityMapper;
import com.phiny.labs.contentmanagement.dto.MultimediaDto;
import com.phiny.labs.contentmanagement.entity.MediaType;
import com.phiny.labs.contentmanagement.entity.Multimedia;
import com.phiny.labs.contentmanagement.exception.MultimediaDoesNotExist;
import com.phiny.labs.contentmanagement.repository.MultimediaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;
import java.util.UUID;

@Service
@Slf4j
public class FileServiceImpl implements IFileService {

    @Value("${s3.bucket.name}")
    private String bucketName;
    @Autowired
    private AmazonS3 s3Client;
    @Autowired
    private MultimediaRepository multimediaRepository;

    @Override
    public MultimediaDto uploadFile(String title, String type, MultipartFile multipartFile) {
        String fileName = uploadFile(multipartFile);
        Multimedia multimedia = null;
        if (fileName != null) {
            multimedia = new Multimedia(title, MediaType.valueOf(type), fileName);
            multimediaRepository.save(multimedia);
        }
        return EntityMapper.INSTANCE.convertToMultimediaDto(multimedia);
    }

    private String uploadFile(MultipartFile multipartFile) {
        try {
            String fileName = UUID.randomUUID() + "_" + multipartFile.getOriginalFilename();
            File file = convertMultiPartFileToFile(multipartFile);
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, fileName, file);
            log.info("Uploading file {} to S3 bucket {}", fileName, bucketName);
            s3Client.putObject(putObjectRequest);
            Files.delete(file.toPath());
            return fileName;
        } catch (IOException e) {
            log.error("Error {} occurred while deleting the file", e.getLocalizedMessage());
        } catch (AmazonServiceException e) {
            log.error("Error {} occurred while uploading the file", e.getLocalizedMessage());
        }
        return null;
    }

    private File convertMultiPartFileToFile(final MultipartFile multipartFile) {
        File file = new File(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            outputStream.write(multipartFile.getBytes());
        } catch (IOException e) {
            log.error("Error {} occurred while converting the multipart file", e.getLocalizedMessage());
        }
        return file;
    }

    @Override
    public S3Object findByFileName(String fileName) {
        log.info("Downloading file with name {}", fileName);
        return s3Client.getObject(bucketName, fileName);
    }

    @Override
    public String getMediaTitle(String fileName) {
        Multimedia multimedia = multimediaRepository.findByUrl(fileName)
                                                    .orElseThrow(() -> new MultimediaDoesNotExist(fileName));
        return multimedia.getTitle();
    }

    @Override
    public void deleteFile(String fileName) {
        multimediaRepository.findByUrl(fileName).ifPresentOrElse(file -> {
            multimediaRepository.delete(file);
            s3Client.deleteObject(bucketName, fileName);
        }, () -> {
            throw new MultimediaDoesNotExist(fileName);
        });
    }

}