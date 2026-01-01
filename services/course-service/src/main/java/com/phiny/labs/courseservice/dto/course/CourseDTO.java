package com.phiny.labs.courseservice.dto.course;

import com.phiny.labs.courseservice.enums.CourseStatus;
import lombok.Data;

import java.sql.Timestamp;
import java.util.UUID;

@Data
public class CourseDTO {
    private UUID id;
    private String title;
    private String description;
    private String code;
    private Timestamp startDate;
    private Timestamp endDate;
    private UUID createdBy;
    private CourseStatus status;
    private Timestamp created;
    private Timestamp updated;
    private Double averageRating;
}

