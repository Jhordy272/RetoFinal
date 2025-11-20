package com.mati.RetoFinal.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.mati.RetoFinal.models.Key;
import com.mati.RetoFinal.models.KeyStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Simplified DTO for caching key information in Redis.
 * This is a lightweight version that excludes heavy fields.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class KeyCacheDto {

    private UUID keyId;
    private String keyValue;
    private String accountNumber;
    private String ownerDocument;
    private KeyStatus status;
    private String entityCode;
    private LocalDateTime createdAt;

    /**
     * Factory method to create from entity
     */
    public static KeyCacheDto fromEntity(Key key) {
        return KeyCacheDto.builder()
                .keyId(key.getKeyId())
                .keyValue(key.getKeyValue())
                .accountNumber(key.getAccountNumber())
                .ownerDocument(key.getOwnerDocument())
                .status(key.getStatus())
                .entityCode(key.getFinancialEntity() != null ? key.getFinancialEntity().getEntityCode() : null)
                .createdAt(key.getCreatedAt())
                .build();
    }
}
