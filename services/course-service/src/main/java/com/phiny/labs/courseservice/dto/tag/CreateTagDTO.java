package com.phiny.labs.courseservice.dto.tag;

import lombok.Data;

import java.util.UUID;

@Data
public class CreateTagDTO {
    private String title;
    private UUID createdBy;
}

