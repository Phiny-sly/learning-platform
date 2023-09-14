package com.turntabl.labs.contentmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MultimediaDto {
    private Long id;
    private String title;
    private String url;
}
