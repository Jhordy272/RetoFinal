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
 * Response DTO for key entity information.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EntityKeyResponse {

    private UUID keyId;
    private String keyValue;
    private String accountNumber;
    private String ownerDocument;
    private KeyStatus status;
    private String entityCode;
    private String entityName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String source;

    /**
     * Factory method to create from entity
     */
    public static EntityKeyResponse fromEntity(Key key, String source) {
        return EntityKeyResponse.builder()
                .keyId(key.getKeyId())
                .keyValue(key.getKeyValue())
                .accountNumber(key.getAccountNumber())
                .ownerDocument(key.getOwnerDocument())
                .status(key.getStatus())
                .entityCode(key.getFinancialEntity() != null ? key.getFinancialEntity().getEntityCode() : null)
                .entityName(key.getFinancialEntity() != null ? key.getFinancialEntity().getEntityName() : null)
                .createdAt(key.getCreatedAt())
                .updatedAt(key.getUpdatedAt())
                .source(source)
                .build();
    }

    /**
     * Factory method to create from cache DTO
     */
    public static EntityKeyResponse fromCache(KeyCacheDto cacheDto) {
        return EntityKeyResponse.builder()
                .keyId(cacheDto.getKeyId())
                .keyValue(cacheDto.getKeyValue())
                .accountNumber(cacheDto.getAccountNumber())
                .ownerDocument(cacheDto.getOwnerDocument())
                .status(cacheDto.getStatus())
                .entityCode(cacheDto.getEntityCode())
                .createdAt(cacheDto.getCreatedAt())
                .source("cache")
                .build();
    }
}
