package com.phiny.labs.courseservice.dto.enrollment;

import lombok.Data;

import java.util.UUID;

@Data
public class CreateEnrollmentDTO {
    private UUID studentId;
    private UUID createdBy;
}

