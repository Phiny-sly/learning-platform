package com.phiny.labs.courseservice.dto.category;

import lombok.Data;

import java.sql.Timestamp;
import java.util.UUID;

@Data
public class CategoryDTO {
    private UUID id;
    private String title;
    private String description;
    private Timestamp created;
    private Timestamp updated;
    private UUID createdBy;
}

