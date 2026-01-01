package com.phiny.labs.courseservice.dto.topic;

import lombok.Data;

import java.sql.Timestamp;
import java.util.UUID;

@Data
public class TopicListDTO {
    private UUID id;
    private String title;
    private String description;
    private UUID courseId;
    private Timestamp created;
}

