package com.turntabl.labs.tenantservice.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorMessage {
    private Integer error_code;
    private String error_message;
}
