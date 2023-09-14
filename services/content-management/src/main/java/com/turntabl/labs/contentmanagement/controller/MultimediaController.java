package com.turntabl.labs.contentmanagement.controller;

import com.amazonaws.services.s3.model.S3Object;
import com.turntabl.labs.contentmanagement.dto.MultimediaDto;
import com.turntabl.labs.contentmanagement.service.IFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/api/multimedia")
public class MultimediaController {

    @Autowired
    private IFileService fileService;

    @PostMapping("/upload")
    public ResponseEntity<MultimediaDto> uploadFile(@RequestParam("title") String title,
                                                    @RequestParam("type") String type,
                                                    @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(fileService.uploadFile(title, type, file));
    }

    @GetMapping("/download/{fileName}")
    public ResponseEntity<InputStreamResource> downloadFile(@PathVariable("fileName") String fileName) {
        S3Object s3Object = fileService.findByFileName(fileName);
        return ResponseEntity
                .ok()
                .cacheControl(CacheControl.noCache())
                .header("Content-type", s3Object.getObjectMetadata().getContentType())
                .header("Content-disposition", "attachment; filename=\"" + fileService.getMediaTitle(fileName) + "\"")
                .body(new InputStreamResource(s3Object.getObjectContent()));
    }

    @GetMapping("/display/{fileName}")
    public ResponseEntity<InputStreamResource> displayFile(@PathVariable("fileName") String fileName) {
        S3Object s3Object = fileService.findByFileName(fileName);
        return ResponseEntity
                .ok()
                .cacheControl(CacheControl.noCache())
                .header("Content-type", s3Object.getObjectMetadata().getContentType())
                .header("Content-disposition", "inline; filename=\"" + fileService.getMediaTitle(fileName) + "\"")
                .body(new InputStreamResource(s3Object.getObjectContent()));
    }

    @DeleteMapping("delete/{fileName}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeFile(@PathVariable("fileName") String fileName) {
        fileService.deleteFile(fileName);
    }
}
