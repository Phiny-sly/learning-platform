package com.phiny.labs.contentmanagement.controller;

import com.amazonaws.services.s3.model.S3Object;
import com.phiny.labs.contentmanagement.client.CourseServiceClient;
import com.phiny.labs.contentmanagement.dto.MultimediaDto;
import com.phiny.labs.contentmanagement.entity.Multimedia;
import com.phiny.labs.contentmanagement.repository.MultimediaRepository;
import com.phiny.labs.contentmanagement.service.IFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/multimedia")
public class MultimediaController {

    @Autowired
    private IFileService fileService;

    @Autowired
    private MultimediaRepository multimediaRepository;

    @Autowired(required = false)
    private CourseServiceClient courseServiceClient;

    @PostMapping("/upload")
    public ResponseEntity<MultimediaDto> uploadFile(
            @RequestParam("title") String title,
            @RequestParam("type") String type,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "courseId", required = false) String courseId,
            @RequestParam(value = "lessonId", required = false) String lessonId,
            @RequestParam(value = "orderIndex", required = false) Integer orderIndex,
            @RequestParam(value = "description", required = false) String description) {
        
        // Validate course exists if courseId is provided
        if (courseId != null && courseServiceClient != null) {
            try {
                java.util.UUID courseUuid = java.util.UUID.fromString(courseId);
                CourseServiceClient.CourseDto course = courseServiceClient.getCourseById(courseUuid);
                if (course == null) {
                    return ResponseEntity.badRequest().build();
                }
            } catch (Exception e) {
                return ResponseEntity.badRequest().build();
            }
        }
        
        MultimediaDto dto = fileService.uploadFile(title, type, file);
        // Update with course/lesson info if provided
        if (courseId != null || lessonId != null || orderIndex != null || description != null) {
            multimediaRepository.findById(dto.getId()).ifPresent(multimedia -> {
                if (courseId != null) multimedia.setCourseId(courseId);
                if (lessonId != null) multimedia.setLessonId(lessonId);
                if (orderIndex != null) multimedia.setOrderIndex(orderIndex);
                if (description != null) multimedia.setDescription(description);
                multimediaRepository.save(multimedia);
                dto.setCourseId(multimedia.getCourseId());
                dto.setLessonId(multimedia.getLessonId());
                dto.setOrderIndex(multimedia.getOrderIndex());
                dto.setDescription(multimedia.getDescription());
            });
        }
        return ResponseEntity.ok(dto);
    }

    @GetMapping
    public ResponseEntity<List<MultimediaDto>> getAllMultimedia() {
        List<MultimediaDto> dtos = multimediaRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MultimediaDto> getMultimediaById(@PathVariable Long id) {
        return multimediaRepository.findById(id)
                .map(this::convertToDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<MultimediaDto>> getMultimediaByCourse(@PathVariable String courseId) {
        List<MultimediaDto> dtos = multimediaRepository.findByCourseIdOrderByOrderIndexAsc(courseId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/lesson/{lessonId}")
    public ResponseEntity<List<MultimediaDto>> getMultimediaByLesson(@PathVariable String lessonId) {
        List<MultimediaDto> dtos = multimediaRepository.findByLessonId(lessonId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
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

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeFile(@PathVariable Long id) {
        multimediaRepository.findById(id).ifPresent(multimedia -> {
            fileService.deleteFile(multimedia.getUrl());
            multimediaRepository.delete(multimedia);
        });
    }

    private MultimediaDto convertToDto(Multimedia multimedia) {
        MultimediaDto dto = new MultimediaDto();
        dto.setId(multimedia.getId());
        dto.setTitle(multimedia.getTitle());
        dto.setMediaType(multimedia.getMediaType());
        dto.setUrl(multimedia.getUrl());
        dto.setCourseId(multimedia.getCourseId());
        dto.setLessonId(multimedia.getLessonId());
        dto.setOrderIndex(multimedia.getOrderIndex());
        dto.setDescription(multimedia.getDescription());
        dto.setCreatedAt(multimedia.getCreatedAt());
        dto.setUpdatedAt(multimedia.getUpdatedAt());
        return dto;
    }
}
