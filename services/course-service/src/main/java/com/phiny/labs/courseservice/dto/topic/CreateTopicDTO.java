package com.phiny.labs.courseservice.dto.topic;

import lombok.Data;

import java.util.UUID;

@Data
public class CreateTopicDTO {
    private String title;
    private String description;
    private UUID courseId;
}

