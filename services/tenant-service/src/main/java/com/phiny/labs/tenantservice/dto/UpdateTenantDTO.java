package com.phiny.labs.tenantservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.annotation.Nullable;
import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateTenantDTO {

    @Nullable private String name;
    @Nullable private String phone;
    @Nullable private String logoUrl;
    @Nullable private Boolean isActive = false;

}
