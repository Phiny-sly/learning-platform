package com.phiny.labs.tenantservice.dto;

import jakarta.annotation.Nullable;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

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
