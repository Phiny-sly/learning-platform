package com.phiny.labs.courseservice.dto.enrollment;

import com.phiny.labs.courseservice.enums.EnrollmentStatus;
import lombok.Data;

import java.sql.Timestamp;
import java.util.UUID;

@Data
public class EnrollmentDTO {
    private UUID id;
    private UUID studentId;
    private UUID courseId;
    private EnrollmentStatus status;
    private Timestamp created;
    private Timestamp updated;
}

