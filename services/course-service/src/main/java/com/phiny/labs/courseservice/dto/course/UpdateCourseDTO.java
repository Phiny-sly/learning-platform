package com.phiny.labs.courseservice.dto.course;

import com.phiny.labs.courseservice.enums.CourseStatus;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class UpdateCourseDTO {
    private String title;
    private String description;
    private String code;
    private Timestamp startDate;
    private Timestamp endDate;
    private CourseStatus status;
}

