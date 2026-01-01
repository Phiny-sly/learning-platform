package com.phiny.labs.contentmanagement.service;

import com.amazonaws.services.s3.model.S3Object;
import com.phiny.labs.contentmanagement.dto.MultimediaDto;
import org.springframework.web.multipart.MultipartFile;

public interface IFileService {
    MultimediaDto uploadFile(String title, String type, MultipartFile multipartFile);

    S3Object findByFileName(String fileName);

    String getMediaTitle(String fileName);

    void deleteFile(String fileName);
}
