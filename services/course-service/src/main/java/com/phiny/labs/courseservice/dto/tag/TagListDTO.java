package com.phiny.labs.courseservice.dto.tag;

import lombok.Data;

import java.sql.Timestamp;
import java.util.UUID;

@Data
public class TagListDTO {
    private UUID id;
    private String title;
    private Timestamp created;
}

