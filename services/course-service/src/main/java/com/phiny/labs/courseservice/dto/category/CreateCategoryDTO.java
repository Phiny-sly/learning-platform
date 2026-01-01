package com.phiny.labs.courseservice.dto.category;

import lombok.Data;

import java.util.UUID;

@Data
public class CreateCategoryDTO {
    private String title;
    private String description;
    private UUID createdBy;
}

