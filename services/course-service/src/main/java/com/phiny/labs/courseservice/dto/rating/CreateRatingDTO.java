package com.phiny.labs.courseservice.dto.rating;

import lombok.Data;

import java.util.UUID;

@Data
public class CreateRatingDTO {
    private UUID courseId;
    private Double rating;
    private String description;
}

