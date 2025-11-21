package com.mati.RetoFinal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateFinancialEntityRequest {

    @NotBlank(message = "Entity code is required")
    @Size(max = 20, message = "Entity code must not exceed 20 characters")
    private String entityCode;

    @NotBlank(message = "Entity name is required")
    @Size(max = 255, message = "Entity name must not exceed 255 characters")
    private String entityName;

    @Size(max = 500, message = "Webhook URL must not exceed 500 characters")
    private String webhookUrl;

    private String webhookAuthConfig;

    @Min(value = 1000, message = "Timeout must be at least 1000ms")
    private Integer timeoutMs;

    @Min(value = 0, message = "Max retries must be non-negative")
    private Integer maxRetries;

    private Boolean isActive;
}
