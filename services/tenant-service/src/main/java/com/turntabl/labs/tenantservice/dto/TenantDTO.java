package com.turntabl.labs.tenantservice.dto;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.sql.Timestamp;
import java.util.UUID;

@Data
@Component
@RequiredArgsConstructor
public class TenantDTO {

    private UUID id;
    private String name;
    private Timestamp created;
    private Timestamp updated;
    private String email;
    private String phone;
    @Nullable private String logoUrl;
    private Boolean isActive;

}
