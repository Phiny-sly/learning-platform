package com.phiny.labs.courseservice.dto.rating;

import lombok.Data;

import java.sql.Timestamp;
import java.util.UUID;

@Data
public class RatingDTO {
    private UUID id;
    private UUID courseId;
    private Double rating;
    private String description;
    private Timestamp created;
    private Timestamp updated;
}

