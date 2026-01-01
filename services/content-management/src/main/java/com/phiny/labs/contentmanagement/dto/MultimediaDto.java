package com.phiny.labs.contentmanagement.dto;

import com.phiny.labs.contentmanagement.entity.MediaType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MultimediaDto {
    private Long id;
    private String title;
    private MediaType mediaType;
    private String url;
    private String courseId;
    private String lessonId;
    private Integer orderIndex;
    private String description;
    private String createdAt;
    private String updatedAt;
}
