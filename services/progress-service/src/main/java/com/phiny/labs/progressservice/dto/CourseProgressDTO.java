package com.phiny.labs.progressservice.dto;

import com.phiny.labs.progressservice.model.ProgressStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class CourseProgressDTO {
    private UUID id;
    private UUID courseId;
    private UUID studentId;
    private Double progressPercentage;
    private Integer completedLessons;
    private Integer totalLessons;
    private ProgressStatus status;
    private LocalDateTime lastAccessedAt;
    private LocalDateTime completedAt;
    private LocalDateTime createdAt;
}

