package com.phiny.labs.progressservice.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class UpdateProgressDTO {
    private UUID lessonId;
    private Integer timeSpentMinutes;
    private Boolean completed;
}

