package com.mati.RetoFinal.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Response DTO for key creation or check operations.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateKeyResponse {

    private boolean created;
    private boolean exists;
    private UUID keyId;
    private String keyValue;
    private double latencyMs;
    private String source;
    private String message;

    /**
     * Factory method for successful key creation
     */
    public static CreateKeyResponse created(UUID keyId, String keyValue, double latencyMs) {
        return CreateKeyResponse.builder()
                .created(true)
                .exists(false)
                .keyId(keyId)
                .keyValue(keyValue)
                .latencyMs(Math.round(latencyMs * 100.0) / 100.0)
                .source("new")
                .build();
    }

    /**
     * Factory method when key already exists
     */
    public static CreateKeyResponse exists(UUID keyId, String keyValue, double latencyMs, String source) {
        return CreateKeyResponse.builder()
                .created(false)
                .exists(true)
                .keyId(keyId)
                .keyValue(keyValue)
                .latencyMs(Math.round(latencyMs * 100.0) / 100.0)
                .source(source)
                .build();
    }

    /**
     * Factory method for error responses
     */
    public static CreateKeyResponse error(String message, double latencyMs) {
        return CreateKeyResponse.builder()
                .created(false)
                .exists(false)
                .latencyMs(Math.round(latencyMs * 100.0) / 100.0)
                .message(message)
                .build();
    }
}
