package com.turntabl.labs.tenantservice.dto;

import jakarta.annotation.Nullable;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;

@Data
@Component
@RequiredArgsConstructor
public class CreateTenantDTO {
    private String name;
    private String email;
    @Nullable private String phone;
    @Nullable private String logoUrl;
    @Nullable private Boolean isActive = false;

}
