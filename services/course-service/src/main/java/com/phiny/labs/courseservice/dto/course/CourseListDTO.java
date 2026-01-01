package com.phiny.labs.courseservice.dto.course;

import com.phiny.labs.courseservice.enums.CourseStatus;
import lombok.Data;

import java.sql.Timestamp;
import java.util.UUID;

@Data
public class CourseListDTO {
    private UUID id;
    private String title;
    private String description;
    private String code;
    private CourseStatus status;
    private Timestamp created;
    private Double averageRating;
}

