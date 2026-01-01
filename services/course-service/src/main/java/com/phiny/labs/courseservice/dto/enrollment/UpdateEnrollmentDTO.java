package com.phiny.labs.courseservice.dto.enrollment;

import com.phiny.labs.courseservice.enums.EnrollmentStatus;
import lombok.Data;

@Data
public class UpdateEnrollmentDTO {
    private EnrollmentStatus status;
}

