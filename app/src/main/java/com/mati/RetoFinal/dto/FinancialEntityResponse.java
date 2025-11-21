package com.mati.RetoFinal.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinancialEntityResponse {

    private UUID entityId;
    private String entityCode;
    private String entityName;
    private String webhookUrl;
    private Integer timeoutMs;
    private Integer maxRetries;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
